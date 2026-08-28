<!-- para IA. não é README de humano. -->
# SPEC — firewall (borda + mapa do sistema)

status: v0.2
sha: `3c920b2`
data: 2026-08-28

## Como usar
- Este arquivo é a fonte. Código ≠ spec → **bug de código**. Spec errada → Ricardo muda **este** arquivo, depois o código.
- IDs estáveis (`REGRA-` `DADOS-` `CONTRATO-` `NÃO-` `GAP-`). Não apague ID; marque `revogado`.
- "achei bug" → cita REGRA/CONTRATO. Se não existir, é GAP, não patch.
- "não estamos salvando X" → olha DADOS. Campo ausente = não é bug.
- "cadastrar campo X" → conflita se quebra REGRA/NÃO; senão vira GAP e só então código.
- GAP = pergunta aberta. Não trate GAP como regra.

## Papel
Único serviço **exposto na internet**. Autentica (UUID em cache neste processo) e encaminha ao MS interno. Não persiste domínio (user/curso/i18). Porta `8080`. Prefixos `/firewall/...`.

## Sistema (mapa)
| repo | porta | exposição |
|---|---|---|
| firewall | 8080 | internet |
| i18n | 8081 | interno |
| login | 8082 | interno |
| user | 8083 | interno |
| course | 8087 | interno |
| perfil | 8088 | interno |
| frontend | static | vitrine |

Token: UUID opaco no header `Authorization`. Cache `HashMap` **na JVM do firewall**. Login MS só **emite** o UUID; não valida sessão. Restart do firewall zera sessão.

Produto: treinamento Java júnior/pleno (Digitus Forum / eusouprogramadorjunior.com). Front: `digitus-forum-frontend`.

## REGRA
- REGRA-EDGE-1: só o firewall fala com a internet. MS 8081–8088 não são API pública.
- REGRA-AUTH-1: token = UUID em cache neste processo. **Não JWT.**
- REGRA-AUTH-2: header `Authorization` tem **duas** partes separadas por espaço; a segunda é o UUID (`Bearer <uuid>`). String vazia ou um único token = inválido.
- REGRA-AUTH-3: **mutação** (create/update/delete/reorder) de curso/módulo/assunto/vídeo/link/user/chat exige token.
- REGRA-AUTH-FREE: leitura de **curso gratuito** (course/module/video/link) na borda é pública, sem token.
- REGRA-AUTH-PAID: leitura de **curso pago** exige token válido + user logado + compra/matrícula daquele `courseId`.
- REGRA-AUTH-4: cadastro/login/reset e **leitura de i18** na borda são públicos. Depois de `validateEmail` / `resetPassword` a borda **emite** token.
- REGRA-CAPTCHA-1: `sendValidationEmail` na borda exige `recaptchaToken` válido (Google siteverify). Secret = env `RECAPTCHA_SECRET`.
- REGRA-AUTH-5: `deleteCache` de i18 exige token.
- REGRA-PROXY-1: firewall não é dono dos dados; grava/lê via MS interno.
- REGRA-ID-1: ids de domínio são UUID string.

## NÃO
- NÃO-JWT: não emite nem aceita bearer/JWT.
- NÃO-DB: sem tabela própria de user/curso/token.
- NÃO-SHUTDOWN: sem `GET/POST /shutdown`.
- NÃO-SUP: sem `/firewall/sup` (removido).
- NÃO-MS-PORT: frontend nunca chama 8081–8088.

## DADOS
Nenhum. Sessão vive só em memória (`uuidCache`). TTL observado no código: `expirationInSeconds = 369000` (~4,3 dias). Ver GAP-TTL.

## CONTRATO (borda)
Público (sem token):
- CONTRATO-LOGIN `POST/ANY /firewall/login/v1/createToken` — emite UUID se email+senha ok
- CONTRATO-EV-SEND ` /firewall/emailVerification/v1/sendValidationEmail`
- CONTRATO-EV-OK ` /firewall/emailVerification/v1/validateEmail` — cria user no MS user **e** devolve token
- CONTRATO-EV-RST-SEND ` /firewall/emailVerification/v1/sendResetPasswordEmail`
- CONTRATO-EV-RST ` /firewall/emailVerification/v1/resetPassword` — troca senha **e** devolve token
- CONTRATO-I18-GET `POST /firewall/internationalization/v1/i18` — lê mensagem por `locale`+`keyy`
- CONTRATO-HEALTH `/firewall/healthCheck`, `/healthCheck`, `/test`, OPTIONS `/**`

Público se o curso é gratuito (`paid=false`); senão REGRA-AUTH-PAID:
- course: `GET retrieveAll` (só gratuitos sem token; com token: gratuitos + comprados) · `retrieveById` · `retrieveSubjectsByCourseId`
- module: `retrieveById` `retrieveByCourseId` `retrieveByCourseIdWithVideos`
- video: `retrieveById` `retrieveBySubjectId`
- link: `retrieveByVideoId`

Exige token sempre:
- user: `POST /firewall/user/v1/create` (não é signup; signup é CONTRATO-EV-OK) · `GET /{id}/retrieve` · `{id}/update` · `{id}/delete`
- chat: `/firewall/user/v1/chat` · `conversations` · `conversation`
- course: `create` · `delete`
- module: `create` `update` `delete` `reorder` `addVideo` `removeVideo`
- subject: `create` `update` `addVideo` `removeVideo` (retrieve* segue a regra grátis/pago do curso)
- video: `create` `update` `delete`
- link: `create` `update` `delete` `reorder`
- i18: `POST /firewall/internationalization/v1/deleteCache`

Não existe na borda (código atual):
- `/firewall/course/v1/retrieveByLocale`
- `/firewall/module/v1/retrieveByTrainingIdWithVideos`
- `/firewall/internationalization/v1/frontend`
- `/firewall/perfil/...`

## Fala com
login `:8082/login/v1/createToken` · user `:8083/user/v1/...` + emailVerification + chat · course `:8087` · i18 `:8081/i18/v1` · perfil `:8088/perfil/v1/retrieve/lastUsed` (URL existe; controller de perfil na borda **não**).

## GAP
- GAP-VITRINE: **revogado** (2026-08-28). Gratuito = público; pago = token + compra.
- GAP-COMPRA: onde persiste “user comprou este courseId” (matrícula). Sem DADOS, REGRA-AUTH-PAID não fecha no código.
- GAP-AUDIO: aula = gif + arquivo de áudio; coluna/chave do áudio ainda não fechada.
- GAP-IDOR-USER: retrieve/update/delete usam `{id}` da URL, não o userId do token. Admin-only? ou só o próprio id?
- GAP-CORS: `@CrossOrigin` / `origins="*"` na borda. Origin permitida ainda não está na spec.
- GAP-TTL: 369000s vs env `TOKEN_EXPIRATION_IN_SECONDS`. Qual vale?
- GAP-PREFIX: login MS devolve UUID cru; borda exige `Bearer <uuid>`. Cliente precisa prefixar.
