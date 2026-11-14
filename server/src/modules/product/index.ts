import { Elysia } from 'elysia'

export const productModule = new Elysia({
  detail: {
    tags: ['Product']
  },
  prefix: '/product'
}).get('/list', () => {
  return 'Product List'
})
