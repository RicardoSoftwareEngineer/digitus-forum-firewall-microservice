<!-- para IA. não é README de humano. -->
# SPEC — firewall

status: v0.10
sha: `f36c738`
data: 2026-09-03

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
- REGRA-MVP1-PAY: Stripe Embedded Checkout. Mensalidade `card`. Avulso **card-only** até Dashboard PIX (GAP-STRIPE-PIX). Não enviar `payment_method_types=pix`. Response `clientSecret` (nunca `sk_`). Ver SPEC-MVP1.md (frontend).
- REGRA-MVP1-STRIPE-TEST: só chaves `sk_test_` / `pk_test_`. Sem `sk_live_` / `pk_live_` até Ricardo pedir. Sem `sk_test_` → 503 em português (mensagens atuais). Catálogo TEST: DADOS-STRIPE-AVULSO-JAVA / DADOS-STRIPE-SUB-JAVA.
- REGRA-MVP1-WEBHOOK: CONTRATO-STRIPE-HOOK verifica `Stripe-Signature` se `STRIPE_WEBHOOK_SECRET` (`whsec_`) estiver no env; senão 503. Não inventar evento. CONTRATO-STRIPE-CONFIRM é a confirmação local (retrieve session) para não depender de URL pública de webhook.

## NÃO
- NÃO-JWT: não emite nem aceita bearer/JWT.
- NÃO-DB: sem tabela própria de user/treinamento/token.
- NÃO-SHUTDOWN: sem `GET/POST /shutdown`.
- NÃO-SUP: sem `/firewall/sup` (removido).
- NÃO-MS-PORT: frontend nunca chama 8081–8088.
- NÃO-PASSWORD: sem `createToken` por email+senha; sem reset de senha.
- NÃO-COOKIE: sessão não é cookie. Header `Authorization` + cache UUID.
- NÃO-PIX-SUB: PIX não é método de mensalidade.
- NÃO-STRIPE-PIX-NOW: avulso também sem PIX até Ricardo ligar PIX no Dashboard test. CONTRATO-STRIPE-BUY é card-only. Não inventar PIX.
- NÃO-STRIPE-SECRET: secret do Stripe só em env, nunca git, nunca o front.
- NÃO-STRIPE-LIVE: sem chave live até Ricardo pedir.
- NÃO-STRIPE-PORTAL: não chama Billing Portal API. Customer portal **não** está configurado.

## DADOS
Nenhum de domínio próprio. Sessão vive só em memória (`uuidCache`). TTL observado no código: `expirationInSeconds = 369000` (~4,3 dias). Ver GAP-TTL.

Catálogo Stripe **TEST** (público, não é secret; só `sk_test_` / `pk_test_`):
- DADOS-STRIPE-AVULSO-JAVA: Avulso Java Pago (teste). `trainingId=c0ffee00-0000-4000-8000-000000000001` `prod_V9v07KbN02y7PV` `price_1U9bA6EXwk40r381is5xrXGd`. Outro training pago sem mapa de price → 400.
- DADOS-STRIPE-SUB-JAVA: Mensalidade guru java. `prod_V9v0HkWMSqlbtZ` `price_1U9bA7EXwk40r38160FozHBx` `interval=month` `guruId=java` R$ 59.

Env (nunca commit de valor): `STRIPE_SECRET_KEY=sk_test_...` `STRIPE_PUBLISHABLE_KEY=pk_test_...` `STRIPE_WEBHOOK_SECRET=whsec_...` (opcional). Recusa `sk_live_` / `pk_live_`.

## CONTRATO (borda)
Público (sem token):
- CONTRATO-STRIPE-HOOK `POST /firewall/billing/v1/stripe/webhook` — público; `Stripe-Signature`. Se `STRIPE_WEBHOOK_SECRET` set: verifica HMAC e upsert DADOS-COMPRA / DADOS-ASSINATURA (`checkout.session.completed` / `invoice.paid` / `customer.subscription.deleted`). Se secret ausente: **503**. Não inventar evento.
- CONTRATO-STRIPE-RETURN `GET /firewall/billing/v1/embedded-return` — HTML simples: «Pagamento enviado. Pode voltar ao curso.» Default `return_url` da Session (file:// não é return_url válido).

- CONTRATO-EV-SEND `POST /firewall/emailVerification/v1/sendValidationEmail` body `{email}` — mock: response inclui `readableNumber`. código alinhado (passthrough; sem captcha enquanto mock).
- CONTRATO-EV-OK `POST /firewall/emailVerification/v1/validateEmail` body `{email, readableNumber}` **sem senha** — cria ou autentica no user MS **e** devolve token (UUID no cache da borda; cliente prefixa `Bearer`). código alinhado (sem senha; sem login MS).
- CONTRATO-I18-GET `POST /firewall/internationalization/v1/i18` — lê mensagem por `locale`+`keyy`
- CONTRATO-FRONT-BUNDLE `POST /firewall/internationalization/v1/frontend` `{locale}` — dump de todas as i18 do locale (público, sem token). Proxy RestTemplate → `/i18/v1/frontend`. JSON array `{keyy, message}`.
- CONTRATO-GURU-PAGES `POST /firewall/guru/v1/{guruId}/pages` — leitura das páginas do guru (público no MVP1, só leitura). Proxy → course MS `/guruPage/v1/retrieveByGuruId`.
- CONTRATO-HEALTH `/firewall/healthCheck`, `/healthCheck`, `/test`, OPTIONS `/**`

**Revogados:**
- CONTRATO-LOGIN ` /firewall/login/v1/createToken` por email+senha — login é CONTRATO-EV-OK
- CONTRATO-EV-RST-SEND `sendResetPasswordEmail`
- CONTRATO-EV-RST `resetPassword`

Público se o treinamento é gratuito (`paid=false`); senão REGRA-AUTH-PAID:
- **Revogado** (2026-08-28): prefixo `/firewall/course/v1` e `retrieve*ByCourseId*`. Equivalente training abaixo.
- training: `GET retrieveAll` (só gratuitos sem token; com token: gratuitos + comprados). JSON de cada training inclui `guruId`, `paid`, `price` (centavos BRL) — DADOS-TRAINING no course MS. · `retrieveById` · `retrieveSubjectsByTrainingId`
- CONTRATO-TRAINING-CATALOG: PaidAccessService.retrieveTraining / requireReadableTraining lê o training via course `POST /training/v1/retrieveCatalogById` `{trainingId}` (sem userId, sem dono). **Não** usa `TRAINING_RETRIEVE_BY_ID`. Gate pago (REGRA-AUTH-PAID) permanece: se paid, token + DADOS-COMPRA/assinatura. Grátis sem token. `retrieveById` da borda (create/delete path) permanece dono.
- module: `retrieveById` `retrieveByTrainingId` `retrieveByTrainingIdWithVideos`
- video: `retrieveById` `retrieveBySubjectId`
- link: `retrieveByVideoId`

Exige token (além do que já está):
- CONTRATO-STRIPE-SUB `POST /firewall/billing/v1/checkout/subscription` — token; se assinatura java já active → 409; senão Session `ui_mode=embedded_page` `mode=subscription` price=`price_1U9bA7EXwk40r38160FozHBx` metadata `userId`+`guruId=java` `payment_method_types=card` `return_url` (REGRA abaixo). Devolve `{clientSecret}` (nunca `sk_`).
- CONTRATO-STRIPE-BUY `POST /firewall/billing/v1/checkout/training` `{trainingId, returnUrl?}` — token; pre-checks (pago, DADOS-COMPRA/assinatura 409, Stripe search already-paid upsert+409). Session `ui_mode=embedded_page` `mode=payment` line_item price=`price_1U9bA6EXwk40r381is5xrXGd` **só** se trainingId = Java Pago teste; outro pago sem mapa → 400; quantity 1; metadata `userId`+`trainingId`; `client_reference_id=userId`; **card-only** (sem `pix`) até GAP-STRIPE-PIX. `return_url`: se `body.returnUrl` é http(s) localhost ou domínio nosso (`eusouprogramadorjunior.com` / `digitusforum.com`), usa; senão CONTRATO-STRIPE-RETURN. file:// inválido. Devolve `{trainingId, clientSecret}` (nunca `sk_`).
- CONTRATO-STRIPE-PK `POST /firewall/billing/v1/publishable-key` — token; `{publishableKey}` de `STRIPE_PUBLISHABLE_KEY` se começa com `pk_test_`; 503 se ausente; 503 se `pk_live_`.
- CONTRATO-STRIPE-CONFIRM `POST /firewall/billing/v1/checkout/confirm` `{sessionId}` — token; retrieve session na Stripe; se `payment_status=paid` (ou subscription complete) upsert DADOS-COMPRA / DADOS-ASSINATURA via BillingRequestService; devolve payload igual CONTRATO-ME. Confirmação local para não depender de URL pública de webhook.
- CONTRATO-ME `POST /firewall/billing/v1/me` — assinatura java + lista de trainingId comprados (DADOS-COMPRA / DADOS-ASSINATURA no user MS; sem Stripe na leitura)

- CONTRATO-BG-SAVE `POST /firewall/background/v1/save` `{name, wallpaperData, dominantColor}` — token; userId do token → user MS
- CONTRATO-BG-LIST `POST /firewall/background/v1/list` — token; lista DADOS-BACKGROUND-SAVE do user
- CONTRATO-BG-SELECT `POST /firewall/background/v1/select` `{backgroundId}` — token; pin + auto=false
- CONTRATO-BG-AUTO `POST /firewall/background/v1/setAuto` — token; auto=true, limpa pin
- CONTRATO-BG-PREFS `POST /firewall/background/v1/prefs` — token; `{backgroundAuto, pinnedBackgroundId, wallpaperData?}`


Exige token sempre:
- user: `POST /firewall/user/v1/create` (não é signup; signup é CONTRATO-EV-OK) · `GET /{id}/retrieve` · `{id}/update` · `{id}/delete`
  - FirewallUserVO: `id`, `name`, `age` (Integer, nullable), `email`. Update body `{name?, age?, email?}` **sem senha**; proxy para user MS. Retrieve/update/delete: PathVariable String + só o próprio userId (PR #30).
  - Meus dados (vitrine): formulário no centro do cinema; borda só faz proxy do VO.
- chat: `/firewall/user/v1/chat` · `conversations` · `conversation`
- training: `create` · `delete`
- module: `create` `update` `delete` `reorder` `addVideo` `removeVideo`
- subject: `create` `update` `addVideo` `removeVideo` (retrieve* segue a regra grátis/pago do treinamento)
- video: `create` `update` `delete`
- link: `create` `update` `delete` `reorder`
- i18: `POST /firewall/internationalization/v1/deleteCache`

Não existe na borda (código atual):
- `/firewall/training/v1/retrieveByLocale` (antes `/firewall/course/v1/retrieveByLocale`)
- `/firewall/perfil/...`

## Fala com
login `:8082/login/v1/createToken` (após código ok; **sem senha**) · user `:8083/user/v1/...` + emailVerification + chat · training `:8087` (repo `digitus-forum-course-microservice`) · i18 `:8081/i18/v1` · perfil `:8088/perfil/v1/retrieve/lastUsed` (URL existe; controller de perfil na borda **não**).

## GAP
- GAP-FRONT-BUNDLE: **revogado** (2026-08-28). CONTRATO-FRONT-BUNDLE.
- GAP-VITRINE: **revogado** (2026-08-28). Gratuito = público; pago = token + compra.
- GAP-COMPRA: **revogado** (MVP1). DADOS-COMPRA / DADOS-ASSINATURA no user MS.
- GAP-COMPRA-OLD: **revogado** (2026-08-28). Matrícula = DADOS-COMPRA / DADOS-ASSINATURA no user MS. REGRA-AUTH-PAID na borda: module `retrieveByTrainingIdWithVideos` e video `retrieveById` exigem token + compra ou assinatura java se `training.paid`.
- GAP-AUDIO: aula = gif + arquivo de áudio; coluna/chave do áudio ainda não fechada.
- GAP-IDOR-USER: **revogado** (PR #30). retrieve/update/delete exigem `{id}` == userId do token.
- GAP-CORS: `@CrossOrigin` / `origins="*"` na borda. Origin permitida ainda não está na spec.
- GAP-TTL: 369000s vs env `TOKEN_EXPIRATION_IN_SECONDS`. Qual vale?
- GAP-PREFIX: login MS devolve UUID cru; borda exige `Bearer <uuid>`. Cliente precisa prefixar.
- GAP-EMAIL-REAL: SES de verdade; parar de ecoar o código; recaptcha no send.
- GAP-STRIPE-PIX: avulso PIX **off** até Ricardo ligar PIX no Dashboard test. CONTRATO-STRIPE-BUY é card-only. Não inventar PIX.
- GAP-STRIPE-PORTAL: Customer portal **não** configurado. Cancelar = Ricardo liga Dashboard **test** → Settings → Billing → Customer portal → Allow customers to cancel. Até lá NÃO-STRIPE-PORTAL (sem Billing Portal API na borda).
