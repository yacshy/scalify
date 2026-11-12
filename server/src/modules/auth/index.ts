import { Elysia } from 'elysia';

export const userModule = new Elysia({
  detail: {
    tags: ['Auth']
  },
  prefix: '/auth'
})
  .get('/login', () => 'Login')
  .get('/register', () => 'Register');
