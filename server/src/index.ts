import { openapi } from "@elysia/openapi";
import { Elysia } from "elysia";

import { OPENAPI_CONFIG } from "@/configs/openapi.config";

import { userModule } from "@/modules/auth";
import { productModule } from "@/modules/product";

const app = new Elysia().use(openapi(OPENAPI_CONFIG)).use(userModule).use(productModule).listen(3000);

console.log(`🦊 Elysia is running at ${app.server?.hostname}:${app.server?.port}`);
