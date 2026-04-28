<template>
  <div class="dashboard-container">
    <h2 class="page-title">数据统计大屏</h2>
    
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.totalArticles || 0 }}</div>
            <div class="stat-label">文章总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <i class="el-icon-success"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.publishedArticles || 0 }}</div>
            <div class="stat-label">已发布</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <i class="el-icon-time"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.pendingArticles || 0 }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f56c6c;">
            <i class="el-icon-view"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ overview.totalVisits || 0 }}</div>
            <div class="stat-label">总访问量</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="chart-box">
          <h3 class="chart-title">访问趋势（近7天）</h3>
          <div ref="visitChart" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-box">
          <h3 class="chart-title">文章发布趋势（近7天）</h3>
          <div ref="articleChart" class="chart"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <div class="chart-box">
          <h3 class="chart-title">栏目文章分布</h3>
          <div ref="categoryChart" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-box">
          <h3 class="chart-title">系统概览</h3>
          <div class="overview-list">
            <div class="overview-item">
              <span class="label">超级管理员</span>
              <span class="value" style="color: #409eff;">1</span>
            </div>
            <div class="overview-item">
              <span class="label">普通管理员</span>
              <span class="value" style="color: #67c23a;">1</span>
            </div>
            <div class="overview-item">
              <span class="label">站点数量</span>
              <span class="value" style="color: #e6a23c;">1</span>
            </div>
            <div class="overview-item">
              <span class="label">栏目数量</span>
              <span class="value" style="color: #909399;">9</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getOverview, getVisitTrend, getArticleTrend, getCategoryStats } from '@/api/statistics'
import * as echarts from 'echarts'

export default {
  name: 'Dashboard',
  data() {
    return {
      overview: {},
      visitChart: null,
      articleChart: null,
      categoryChart: null
    }
  },
  mounted() {
    this.initCharts()
    this.loadData()
  },
  methods: {
    initCharts() {
      this.visitChart = echarts.init(this.$refs.visitChart)
      this.articleChart = echarts.init(this.$refs.articleChart)
      this.categoryChart = echarts.init(this.$refs.categoryChart)
      
      window.addEventListener('resize', () => {
        this.visitChart.resize()
        this.articleChart.resize()
        this.categoryChart.resize()
      })
    },
    async loadData() {
      try {
        const [overviewRes, visitRes, articleRes, categoryRes] = await Promise.all([
          getOverview(),
          getVisitTrend(7),
          getArticleTrend(7),
          getCategoryStats()
        ])
        
        this.overview = overviewRes.data
        this.renderVisitChart(visitRes.data)
        this.renderArticleChart(articleRes.data)
        this.renderCategoryChart(categoryRes.data)
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    renderVisitChart(data) {
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: data.map(item => item.date),
          axisLine: { lineStyle: { color: '#999' } }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#999' } }
        },
        series: [{
          data: data.map(item => item.count),
          type: 'line',
          smooth: true,
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
            ])
          },
          lineStyle: { color: '#409eff', width: 2 },
          itemStyle: { color: '#409eff' }
        }]
      }
      this.visitChart.setOption(option)
    },
    renderArticleChart(data) {
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: data.map(item => item.date),
          axisLine: { lineStyle: { color: '#999' } }
        },
        yAxis: {
          type: 'value',
          axisLine: { lineStyle: { color: '#999' } }
        },
        series: [{
          data: data.map(item => item.count),
          type: 'bar',
          barWidth: '50%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#67c23a' },
              { offset: 1, color: '#529b2e' }
            ]),
            borderRadius: [4, 4, 0, 0]
          }
        }]
      }
      this.articleChart.setOption(option)
    },
    renderCategoryChart(data) {
      const option = {
        tooltip: { trigger: 'item' },
        legend: {
          orient: 'vertical',
          right: '5%',
          top: 'center'
        },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 14, fontWeight: 'bold' }
          },
          data: data.map((item, index) => ({
            name: `栏目${item.categoryId}`,
            value: item.count,
            itemStyle: {
              color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399'][index % 5]
            }
          }))
        }]
      }
      this.categoryChart.setOption(option)
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}

.stat-card {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-box {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.chart-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 15px;
  color: #303133;
}

.chart {
  height: 250px;
}

.overview-list {
  padding: 10px 0;
}

.overview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f4f4f5;
}

.overview-item:last-child {
  border-bottom: none;
}

.overview-item .label {
  font-size: 14px;
  color: #606266;
}

.overview-item .value {
  font-size: 20px;
  font-weight: 600;
}
</style>
