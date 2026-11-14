import { openapi } from '@elysia/openapi'
import { MastraServer } from '@mastra/elysia'
import { Elysia } from 'elysia'
import { OPENAPI_CONFIG } from '@/configs/openapi.config'
import { mastra } from '@/mastra'
import { userModule } from '@/modules/auth'
import { productModule } from '@/modules/product'

const app = new Elysia()

const server = new MastraServer({ app, mastra })
await server.init()

app.use(openapi(OPENAPI_CONFIG)).use(userModule).use(productModule)
app.listen(3000)

console.log(`🦊 Elysia is running at ${app.server?.hostname}:${app.server?.port}`)
