<!-- para IA. não é README de humano. -->
# SPEC — firewall

status: v0.6
sha: `5b8cf60`
data: 2026-08-28

## Como usar
- Este arquivo é a fonte. Código ≠ spec → **bug de código**. Spec errada → Ricardo muda **este** arquivo, depois o código.
- IDs estáveis (`REGRA-` `DADOS-` `CONTRATO-` `NÃO-` `GAP-`). Não apague ID; marque `revogado`.
- "achei bug" → cita REGRA/CONTRATO. Se não existir, é GAP, não patch.
- "não estamos salvando X" → olha DADOS. Campo ausente = não é bug.
- "cadastrar campo X" → conflita se quebra REGRA/NÃO; senão vira GAP e só então código.
- GAP = pergunta aberta. Não trate GAP como regra.

## Papel
Única borda na internet (porta `8080`). Proxy + cache de token UUID neste processo. Não é dono de User/Training/i18n.

## REGRA
- REGRA-EDGE-1: só o firewall fala com a internet. MS 8081–8088 não são API pública.
- REGRA-AUTH-1: token = UUID em cache neste processo. **Não JWT.**
- REGRA-AUTH-2: header `Authorization` tem **duas** partes separadas por espaço; a segunda é o UUID (`Bearer <uuid>`). String vazia ou um único token = inválido. Token **não** vai em cookie (front: `localStorage`; ver frontend REGRA-TOKEN-STORE).
- REGRA-AUTH-3: **mutação** (create/update/delete/reorder) de treinamento/módulo/assunto/vídeo/link/user/chat exige token.
- REGRA-AUTH-FREE: leitura de **treinamento gratuito** (training/module/video/link) na borda é pública, sem token. Gratuito = paid=false AND price=0 (DADOS-TRAINING no course MS).
- REGRA-AUTH-PAID: leitura de **treinamento pago** exige token válido + user logado + (DADOS-COMPRA daquele `trainingId` **ou** DADOS-ASSINATURA active do guru do training). MVP1: assinatura = guru java.
- REGRA-AUTH-4: pedido de código e validação de código na borda são **públicos**. Depois de CONTRATO-EV-OK a borda **emite** token (chama login MS **sem senha**).
- REGRA-AUTH-CODE: cadastro e login = email + código. **Sem senha.** Um fluxo só: email novo cria user; existente entra.
- REGRA-EMAIL-MOCK: `sendValidationEmail` **devolve** `readableNumber` no JSON (não envia SES). Quando GAP-EMAIL-REAL, parar de ecoar o código.
- REGRA-CAPTCHA-1: **revogado** (2026-08-28) enquanto REGRA-EMAIL-MOCK. Recaptcha volta com GAP-EMAIL-REAL.
- REGRA-AUTH-5: `deleteCache` de i18 exige token.
- REGRA-PROXY-1: firewall não é dono dos dados; grava/lê via MS interno.
- REGRA-ID-1: ids de domínio são UUID string.
- REGRA-GURU-HOST: um domínio, um front. Borda não roteia por host de guru.
- REGRA-MVP1-PAY: Stripe Embedded Checkout. Mensalidade `card`. Avulso `card`+`pix`. Response `clientSecret`. Ver SPEC-MVP1.md (frontend).
- REGRA-MVP1-STRIPE-TEST: só chaves `sk_test_` / `pk_test_`. Sem `sk_live_` até Ricardo pedir.
- REGRA-MVP1-WEBHOOK: webhook Stripe na borda; verificar assinatura; só então user MS grava entitlement.

## NÃO
- NÃO-JWT: não emite nem aceita bearer/JWT.
- NÃO-DB: sem tabela própria de user/treinamento/token.
- NÃO-SHUTDOWN: sem `GET/POST /shutdown`.
- NÃO-SUP: sem `/firewall/sup` (removido).
- NÃO-MS-PORT: frontend nunca chama 8081–8088.
- NÃO-PASSWORD: sem `createToken` por email+senha; sem reset de senha.
- NÃO-COOKIE: sessão não é cookie. Header `Authorization` + cache UUID.
- NÃO-PIX-SUB: PIX não é método de mensalidade.
- NÃO-STRIPE-SECRET: secret do Stripe só em env, nunca git, nunca o front.
- NÃO-STRIPE-LIVE: sem chave live até Ricardo pedir.

## DADOS
Nenhum. Sessão vive só em memória (`uuidCache`). TTL observado no código: `expirationInSeconds = 369000` (~4,3 dias). Ver GAP-TTL.

## CONTRATO (borda)
Público (sem token):
- CONTRATO-STRIPE-HOOK `POST /firewall/billing/v1/stripe/webhook` — Stripe-Signature; sem token de aluno

- CONTRATO-EV-SEND `POST /firewall/emailVerification/v1/sendValidationEmail` body `{email}` — mock: response inclui `readableNumber`. código alinhado (passthrough; sem captcha enquanto mock).
- CONTRATO-EV-OK `POST /firewall/emailVerification/v1/validateEmail` body `{email, readableNumber}` **sem senha** — cria ou autentica no user MS **e** devolve token (UUID no cache da borda; cliente prefixa `Bearer`). código alinhado (sem senha; sem login MS).
- CONTRATO-I18-GET `POST /firewall/internationalization/v1/i18` — lê mensagem por `locale`+`keyy`
- CONTRATO-HEALTH `/firewall/healthCheck`, `/healthCheck`, `/test`, OPTIONS `/**`

**Revogados:**
- CONTRATO-LOGIN ` /firewall/login/v1/createToken` por email+senha — login é CONTRATO-EV-OK
- CONTRATO-EV-RST-SEND `sendResetPasswordEmail`
- CONTRATO-EV-RST `resetPassword`

Público se o treinamento é gratuito (`paid=false`); senão REGRA-AUTH-PAID:
- **Revogado** (2026-08-28): prefixo `/firewall/course/v1` e `retrieve*ByCourseId*`. Equivalente training abaixo.
- training: `GET retrieveAll` (só gratuitos sem token; com token: gratuitos + comprados). JSON de cada training inclui `guruId`, `paid`, `price` (centavos BRL) — DADOS-TRAINING no course MS. · `retrieveById` · `retrieveSubjectsByTrainingId`
- module: `retrieveById` `retrieveByTrainingId` `retrieveByTrainingIdWithVideos`
- video: `retrieveById` `retrieveBySubjectId`
- link: `retrieveByVideoId`

Exige token (além do que já está):
- CONTRATO-STRIPE-SUB `POST /firewall/billing/v1/checkout/subscription` — Session embedded mensalidade java, `card`, devolve `clientSecret`
- CONTRATO-STRIPE-BUY `POST /firewall/billing/v1/checkout/training` `{trainingId}` — Session embedded avulsa `card`+`pix`, devolve `clientSecret`
- CONTRATO-ME `GET /firewall/billing/v1/me` — assinatura + lista de trainingId comprados
- CONTRATO-GURU-PAGES leitura das páginas do guru (público no MVP1)

Exige token sempre:
- user: `POST /firewall/user/v1/create` (não é signup; signup é CONTRATO-EV-OK) · `GET /{id}/retrieve` · `{id}/update` · `{id}/delete`
- chat: `/firewall/user/v1/chat` · `conversations` · `conversation`
- training: `create` · `delete`
- module: `create` `update` `delete` `reorder` `addVideo` `removeVideo`
- subject: `create` `update` `addVideo` `removeVideo` (retrieve* segue a regra grátis/pago do treinamento)
- video: `create` `update` `delete`
- link: `create` `update` `delete` `reorder`
- i18: `POST /firewall/internationalization/v1/deleteCache`

Não existe na borda (código atual):
- `/firewall/training/v1/retrieveByLocale` (antes `/firewall/course/v1/retrieveByLocale`)
- `/firewall/internationalization/v1/frontend`
- `/firewall/perfil/...`

## Fala com
login `:8082/login/v1/createToken` (após código ok; **sem senha**) · user `:8083/user/v1/...` + emailVerification + chat · training `:8087` (repo `digitus-forum-course-microservice`) · i18 `:8081/i18/v1` · perfil `:8088/perfil/v1/retrieve/lastUsed` (URL existe; controller de perfil na borda **não**).

## GAP
- GAP-VITRINE: **revogado** (2026-08-28). Gratuito = público; pago = token + compra.
- GAP-COMPRA: **revogado** (MVP1). DADOS-COMPRA / DADOS-ASSINATURA no user MS.
- GAP-COMPRA-OLD: onde persiste “user comprou este trainingId” (matrícula). Sem DADOS, REGRA-AUTH-PAID não fecha no código.
- GAP-AUDIO: aula = gif + arquivo de áudio; coluna/chave do áudio ainda não fechada.
- GAP-IDOR-USER: retrieve/update/delete usam `{id}` da URL, não o userId do token. Admin-only? ou só o próprio id?
- GAP-CORS: `@CrossOrigin` / `origins="*"` na borda. Origin permitida ainda não está na spec.
- GAP-TTL: 369000s vs env `TOKEN_EXPIRATION_IN_SECONDS`. Qual vale?
- GAP-PREFIX: login MS devolve UUID cru; borda exige `Bearer <uuid>`. Cliente precisa prefixar.
- GAP-EMAIL-REAL: SES de verdade; parar de ecoar o código; recaptcha no send.
