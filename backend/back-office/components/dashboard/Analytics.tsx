import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Progress } from '../ui/progress';
import { Badge } from '../ui/badge';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
} from 'recharts';
import {
  TrendingUp,
  TrendingDown,
  Users,
  Eye,
  Clock,
  Download,
} from 'lucide-react';

const dailyData = [
  { name: '월', 방문자: 2400, 페이지뷰: 4000, 체류시간: 3.2 },
  { name: '화', 방문자: 1398, 페이지뷰: 3000, 체류시간: 2.8 },
  { name: '수', 방문자: 9800, 페이지뷰: 2000, 체류시간: 4.1 },
  { name: '목', 방문자: 3908, 페이지뷰: 2780, 체류시간: 3.5 },
  { name: '금', 방문자: 4800, 페이지뷰: 1890, 체류시간: 2.9 },
  { name: '토', 방문자: 3800, 페이지뷰: 2390, 체류시간: 3.8 },
  { name: '일', 방문자: 4300, 페이지뷰: 3490, 체류시간: 4.2 },
];

const deviceData = [
  { name: '데스크톱', value: 45, color: '#8884d8' },
  { name: '모바일', value: 35, color: '#82ca9d' },
  { name: '태블릿', value: 20, color: '#ffc658' },
];

const topPages = [
  { path: '/dashboard', views: 15420, change: 12.5 },
  { path: '/users', views: 8930, change: -3.2 },
  { path: '/content', views: 7650, change: 8.1 },
  { path: '/analytics', views: 5480, change: 15.7 },
  { path: '/settings', views: 3210, change: -1.4 },
];

export function Analytics() {
  const totalVisitors = dailyData.reduce((sum, day) => sum + day.방문자, 0);
  const totalPageviews = dailyData.reduce((sum, day) => sum + day.페이지뷰, 0);
  const averageTime = dailyData.reduce((sum, day) => sum + day.체류시간, 0) / dailyData.length;

  return (
    <div className="space-y-6">
      {/* 기간 선택 */}
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold">웹사이트 분석</h2>
          <p className="text-muted-foreground">사이트 트래픽과 사용자 행동을 분석합니다.</p>
        </div>
        <div className="flex items-center space-x-4">
          <Select defaultValue="7days">
            <SelectTrigger className="w-32">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="today">오늘</SelectItem>
              <SelectItem value="7days">7일</SelectItem>
              <SelectItem value="30days">30일</SelectItem>
              <SelectItem value="90days">90일</SelectItem>
            </SelectContent>
          </Select>
          <Button variant="outline">
            <Download className="h-4 w-4 mr-2" />
            리포트 다운로드
          </Button>
        </div>
      </div>

      {/* 주요 지표 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">총 방문자</p>
                <p className="text-2xl font-bold">{totalVisitors.toLocaleString()}</p>
                <div className="flex items-center text-sm text-green-600">
                  <TrendingUp className="h-4 w-4 mr-1" />
                  +12.5% 전주 대비
                </div>
              </div>
              <Users className="h-8 w-8 text-blue-500" />
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">페이지뷰</p>
                <p className="text-2xl font-bold">{totalPageviews.toLocaleString()}</p>
                <div className="flex items-center text-sm text-green-600">
                  <TrendingUp className="h-4 w-4 mr-1" />
                  +8.2% 전주 대비
                </div>
              </div>
              <Eye className="h-8 w-8 text-green-500" />
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">평균 체류시간</p>
                <p className="text-2xl font-bold">{averageTime.toFixed(1)}분</p>
                <div className="flex items-center text-sm text-red-600">
                  <TrendingDown className="h-4 w-4 mr-1" />
                  -2.1% 전주 대비
                </div>
              </div>
              <Clock className="h-8 w-8 text-orange-500" />
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-muted-foreground">이탈률</p>
                <p className="text-2xl font-bold">34.2%</p>
                <div className="flex items-center text-sm text-green-600">
                  <TrendingUp className="h-4 w-4 mr-1" />
                  -3.8% 전주 대비
                </div>
              </div>
              <div className="h-8 w-8 rounded-full bg-red-100 flex items-center justify-center">
                <span className="text-red-600 font-semibold">%</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* 일별 트래픽 차트 */}
        <Card>
          <CardHeader>
            <CardTitle>일별 트래픽</CardTitle>
            <CardDescription>지난 7일간의 방문자와 페이지뷰 추이</CardDescription>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={dailyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="방문자" fill="#8884d8" />
                <Bar dataKey="페이지뷰" fill="#82ca9d" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 디바이스별 분포 */}
        <Card>
          <CardHeader>
            <CardTitle>디바이스별 접속 현황</CardTitle>
            <CardDescription>사용자가 사용하는 디바이스 분포</CardDescription>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={deviceData}
                  cx="50%"
                  cy="50%"
                  outerRadius={100}
                  fill="#8884d8"
                  dataKey="value"
                  label={({ name, value }) => `${name} ${value}%`}
                >
                  {deviceData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip />
              </PieChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* 인기 페이지 */}
      <Card>
        <CardHeader>
          <CardTitle>인기 페이지</CardTitle>
          <CardDescription>가장 많이 방문된 페이지 목록</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {topPages.map((page, index) => (
              <div key={index} className="flex items-center justify-between p-3 border rounded-lg">
                <div className="flex items-center space-x-3">
                  <Badge variant="outline">{index + 1}</Badge>
                  <div>
                    <p className="font-medium">{page.path}</p>
                    <p className="text-sm text-muted-foreground">
                      {page.views.toLocaleString()} 조회
                    </p>
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <span
                    className={`text-sm ${
                      page.change > 0 ? 'text-green-600' : 'text-red-600'
                    }`}
                  >
                    {page.change > 0 ? '+' : ''}{page.change}%
                  </span>
                  {page.change > 0 ? (
                    <TrendingUp className="h-4 w-4 text-green-600" />
                  ) : (
                    <TrendingDown className="h-4 w-4 text-red-600" />
                  )}
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}