<!-- para IA. não é README de humano. -->
# SPEC — firewall (borda + mapa do sistema)

status: v0
sha: `3c920b2`
data: 2026-08-28

## Como usar
- Este arquivo é a fonte. Código ≠ spec → **bug de código**. Spec errada → Ricardo muda **este** arquivo, depois o código.
- IDs estáveis (`INV-` `DADOS-` `END-` `NÃO-` `GAP-`). Não apague ID; marque `revogado`.
- "achei bug" → cita INV/END. Se não existir, é GAP, não patch.
- "não estamos salvando X" → olha DADOS. Campo ausente = não é bug.
- "cadastrar campo X" → conflita se quebra INV/NÃO; senão vira GAP e só então código.
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

## INV
- INV-EDGE-1: só o firewall fala com a internet. MS 8081–8088 não são API pública.
- INV-AUTH-1: token = UUID em cache neste processo. **Não JWT.**
- INV-AUTH-2: header `Authorization` = o UUID. Vazio/ausente = não autenticado.
- INV-AUTH-3: mutação e leitura de curso/módulo/assunto/vídeo/link/user/chat exigem token válido.
- INV-AUTH-4: cadastro/login/reset e **leitura de i18** na borda são públicos (sem token). Depois de `validateEmail` / `resetPassword` a borda **emite** token.
- INV-AUTH-5: `deleteCache` de i18 exige token.
- INV-PROXY-1: firewall não é dono dos dados; grava/lê via MS interno.
- INV-ID-1: ids de domínio são UUID string.

## NÃO
- NÃO-JWT: não emite nem aceita bearer/JWT.
- NÃO-DB: sem tabela própria de user/curso/token.
- NÃO-SHUTDOWN: sem `GET/POST /shutdown`.
- NÃO-SUP: sem `/firewall/sup` (removido).
- NÃO-MS-PORT: frontend nunca chama 8081–8088.

## DADOS
Nenhum. Sessão vive só em memória (`uuidCache`). TTL observado no código: `expirationInSeconds = 369000` (~4,3 dias). Ver GAP-TTL.

## END (borda)
Público (sem token):
- END-LOGIN `POST/ANY /firewall/login/v1/createToken` — emite UUID se email+senha ok
- END-EV-SEND ` /firewall/emailVerification/v1/sendValidationEmail`
- END-EV-OK ` /firewall/emailVerification/v1/validateEmail` — cria user no MS user **e** devolve token
- END-EV-RST-SEND ` /firewall/emailVerification/v1/sendResetPasswordEmail`
- END-EV-RST ` /firewall/emailVerification/v1/resetPassword` — troca senha **e** devolve token
- END-I18-GET `POST /firewall/internationalization/v1/i18` — lê mensagem por `locale`+`keyy`
- END-HEALTH `/firewall/healthCheck`, `/healthCheck`, `/test`, OPTIONS `/**`

Exige token (`validateToken`):
- user: `POST /firewall/user/v1/create` · `GET /firewall/user/v1/{id}/retrieve` · `{id}/update` · `{id}/delete`
- chat: `/firewall/user/v1/chat` · `conversations` · `conversation`
- course: `create` · `GET retrieveAll` · `retrieveById` · `retrieveSubjectsByCourseId` · `delete`
- module: `create` `retrieveById` `retrieveByCourseId` `retrieveByCourseIdWithVideos` `update` `delete` `reorder` `addVideo` `removeVideo`
- subject: `create` `update` `retrieveByVideo` `retrieveByCourseId` `retrieveByIdWithVideos` `addVideo` `removeVideo`
- video: `create` `retrieveById` `retrieveBySubjectId` `update` `delete`
- link: `create` `retrieveByVideoId` `update` `delete` `reorder`
- i18: `POST /firewall/internationalization/v1/deleteCache`

Não existe na borda (código atual):
- `/firewall/course/v1/retrieveByLocale`
- `/firewall/module/v1/retrieveByTrainingIdWithVideos`
- `/firewall/internationalization/v1/frontend`
- `/firewall/perfil/...`

## Fala com
login `:8082/login/v1/createToken` · user `:8083/user/v1/...` + emailVerification + chat · course `:8087` · i18 `:8081/i18/v1` · perfil `:8088/perfil/v1/retrieve/lastUsed` (URL existe; controller de perfil na borda **não**).

## GAP
- GAP-VITRINE: vitrine chama APIs **sem** token e paths que **não existem** na borda. Spec de produto: a vitrine é pública? se sim, quais ENDs ficam públicos; se não, o front precisa de login. **Não inventar login no código até fechar isto.**
- GAP-IDOR-USER: retrieve/update/delete usam `{id}` da URL, não o userId do token. Admin-only? ou só o próprio id?
- GAP-CORS: `@CrossOrigin` / `origins="*"` na borda. Origin permitida ainda não está na spec.
- GAP-TTL: 369000s vs env `TOKEN_EXPIRATION_IN_SECONDS`. Qual vale?
- GAP-CAPTCHA: recaptcha na borda em alguns fluxos; regra de quando é obrigatório não está fechada.
