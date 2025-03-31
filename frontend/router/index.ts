import { createRouter, createWebHistory } from 'vue-router'
import Ticker from '../src/components/Ticker.vue'
import Transactions from '@/components/Transactions.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Ticker
  },
  {
    path: '/history',
    name: 'Transactions',
    component: Transactions
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
