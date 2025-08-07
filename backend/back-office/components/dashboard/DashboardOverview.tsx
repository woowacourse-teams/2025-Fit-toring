import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Progress } from '../ui/progress';
import { Badge } from '../ui/badge';
import {
  Users,
  FileText,
  TrendingUp,
  Activity,
  Eye,
  Clock,
  AlertCircle,
  CheckCircle,
} from 'lucide-react';

export function DashboardOverview() {
  const stats = [
    {
      title: '총 사용자',
      value: '2,847',
      change: '+12%',
      changeType: 'positive' as const,
      icon: Users,
    },
    {
      title: '총 콘텐츠',
      value: '1,234',
      change: '+5%',
      changeType: 'positive' as const,
      icon: FileText,
    },
    {
      title: '월간 조회수',
      value: '45.2K',
      change: '+18%',
      changeType: 'positive' as const,
      icon: Eye,
    },
    {
      title: '시스템 상태',
      value: '99.2%',
      change: '-0.1%',
      changeType: 'negative' as const,
      icon: Activity,
    },
  ];

  const recentActivities = [
    { action: '새 사용자 가입', user: 'user@example.com', time: '5분 전', status: 'success' },
    { action: '콘텐츠 업로드', user: 'editor@example.com', time: '12분 전', status: 'success' },
    { action: '시스템 백업 완료', user: 'system', time: '1시간 전', status: 'success' },
    { action: '로그인 실패 감지', user: 'unknown', time: '2시간 전', status: 'warning' },
    { action: '콘텐츠 승인 대기', user: 'writer@example.com', time: '3시간 전', status: 'pending' },
  ];

  return (
    <div className="space-y-6">
      {/* 상단 통계 카드 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {stats.map((stat, index) => (
          <Card key={index}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
              <stat.icon className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stat.value}</div>
              <div className="flex items-center space-x-2 text-xs text-muted-foreground">
                <TrendingUp
                  className={`h-4 w-4 ${
                    stat.changeType === 'positive' ? 'text-green-500' : 'text-red-500'
                  }`}
                />
                <span
                  className={
                    stat.changeType === 'positive' ? 'text-green-500' : 'text-red-500'
                  }
                >
                  {stat.change}
                </span>
                <span>지난 달 대비</span>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* 시스템 상태 */}
        <Card>
          <CardHeader>
            <CardTitle>시스템 상태</CardTitle>
            <CardDescription>서버 리소스 사용률</CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-sm">CPU 사용률</span>
                <span className="text-sm">45%</span>
              </div>
              <Progress value={45} />
            </div>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-sm">메모리 사용률</span>
                <span className="text-sm">72%</span>
              </div>
              <Progress value={72} />
            </div>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-sm">디스크 사용률</span>
                <span className="text-sm">38%</span>
              </div>
              <Progress value={38} />
            </div>
            <div className="space-y-2">
              <div className="flex justify-between">
                <span className="text-sm">네트워크</span>
                <span className="text-sm">23%</span>
              </div>
              <Progress value={23} />
            </div>
          </CardContent>
        </Card>

        {/* 최근 활동 */}
        <Card>
          <CardHeader>
            <CardTitle>최근 활동</CardTitle>
            <CardDescription>시스템에서 발생한 최근 이벤트</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {recentActivities.map((activity, index) => (
                <div key={index} className="flex items-center space-x-3">
                  <div className="flex-shrink-0">
                    {activity.status === 'success' && (
                      <CheckCircle className="h-4 w-4 text-green-500" />
                    )}
                    {activity.status === 'warning' && (
                      <AlertCircle className="h-4 w-4 text-orange-500" />
                    )}
                    {activity.status === 'pending' && (
                      <Clock className="h-4 w-4 text-blue-500" />
                    )}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium">{activity.action}</p>
                    <p className="text-xs text-muted-foreground">{activity.user}</p>
                  </div>
                  <div className="flex-shrink-0">
                    <span className="text-xs text-muted-foreground">{activity.time}</span>
                  </div>
                </div>
              ))}
            </div>
            <div className="pt-4">
              <Button variant="outline" className="w-full">
                모든 활동 보기
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 빠른 작업 */}
      <Card>
        <CardHeader>
          <CardTitle>빠른 작업</CardTitle>
          <CardDescription>자주 사용하는 관리 작업</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <Button variant="outline" className="h-20 flex-col">
              <Users className="h-6 w-6 mb-2" />
              새 사용자 추가
            </Button>
            <Button variant="outline" className="h-20 flex-col">
              <FileText className="h-6 w-6 mb-2" />
              콘텐츠 업로드
            </Button>
            <Button variant="outline" className="h-20 flex-col">
              <Activity className="h-6 w-6 mb-2" />
              시스템 점검
            </Button>
            <Button variant="outline" className="h-20 flex-col">
              <AlertCircle className="h-6 w-6 mb-2" />
              알림 관리
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}