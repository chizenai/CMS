import request from '@/utils/request'

export function getOverview() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

export function getVisitTrend(days) {
  return request({
    url: '/statistics/visit-trend',
    method: 'get',
    params: { days }
  })
}

export function getArticleTrend(days) {
  return request({
    url: '/statistics/article-trend',
    method: 'get',
    params: { days }
  })
}

export function getCategoryStats() {
  return request({
    url: '/statistics/category-stats',
    method: 'get'
  })
}
