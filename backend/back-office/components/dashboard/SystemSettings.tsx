import { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Switch } from '../ui/switch';
import { Textarea } from '../ui/textarea';
import { Separator } from '../ui/separator';
import { Badge } from '../ui/badge';
import { Progress } from '../ui/progress';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select';
import {
  Tabs,
  TabsContent,
  TabsList,
  TabsTrigger,
} from '../ui/tabs';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '../ui/alert-dialog';
import {
  Settings,
  Shield,
  Database,
  Mail,
  Globe,
  Users,
  Save,
  RefreshCw,
  Trash2,
  Download,
  Upload,
} from 'lucide-react';

export function SystemSettings() {
  const [isLoading, setIsLoading] = useState(false);
  const [settings, setSettings] = useState({
    siteName: 'Back Office System',
    siteUrl: 'https://backoffice.example.com',
    adminEmail: 'admin@example.com',
    maintenanceMode: false,
    userRegistration: true,
    emailNotifications: true,
    autoBackup: true,
    sessionTimeout: 30,
    maxFileSize: 10,
    defaultLanguage: 'ko',
    timezone: 'Asia/Seoul',
  });

  const handleSave = async () => {
    setIsLoading(true);
    // 시뮬레이션 저장
    await new Promise(resolve => setTimeout(resolve, 1500));
    setIsLoading(false);
  };

  const systemInfo = [
    { label: '서버 OS', value: 'Ubuntu 20.04 LTS' },
    { label: '웹 서버', value: 'Nginx 1.18.0' },
    { label: '데이터베이스', value: 'PostgreSQL 13.7' },
    { label: '메모리 사용량', value: '2.4GB / 8GB', progress: 30 },
    { label: '디스크 사용량', value: '45GB / 100GB', progress: 45 },
    { label: 'CPU 사용률', value: '12%', progress: 12 },
  ];

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h2 className="text-2xl font-bold">시스템 설정</h2>
          <p className="text-muted-foreground">시스템 전반적인 설정을 관리합니다.</p>
        </div>
        <Button onClick={handleSave} disabled={isLoading}>
          {isLoading ? (
            <RefreshCw className="h-4 w-4 mr-2 animate-spin" />
          ) : (
            <Save className="h-4 w-4 mr-2" />
          )}
          설정 저장
        </Button>
      </div>

      <Tabs defaultValue="general" className="space-y-6">
        <TabsList className="grid w-full grid-cols-5">
          <TabsTrigger value="general">일반</TabsTrigger>
          <TabsTrigger value="security">보안</TabsTrigger>
          <TabsTrigger value="email">이메일</TabsTrigger>
          <TabsTrigger value="system">시스템</TabsTrigger>
          <TabsTrigger value="backup">백업</TabsTrigger>
        </TabsList>

        <TabsContent value="general" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <Globe className="h-5 w-5" />
                <span>사이트 정보</span>
              </CardTitle>
              <CardDescription>기본적인 사이트 정보를 설정합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="siteName">사이트 이름</Label>
                  <Input
                    id="siteName"
                    value={settings.siteName}
                    onChange={(e) => setSettings({...settings, siteName: e.target.value})}
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="siteUrl">사이트 URL</Label>
                  <Input
                    id="siteUrl"
                    value={settings.siteUrl}
                    onChange={(e) => setSettings({...settings, siteUrl: e.target.value})}
                  />
                </div>
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="adminEmail">관리자 이메일</Label>
                <Input
                  id="adminEmail"
                  type="email"
                  value={settings.adminEmail}
                  onChange={(e) => setSettings({...settings, adminEmail: e.target.value})}
                />
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="language">기본 언어</Label>
                  <Select value={settings.defaultLanguage} onValueChange={(value) => setSettings({...settings, defaultLanguage: value})}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="ko">한국어</SelectItem>
                      <SelectItem value="en">English</SelectItem>
                      <SelectItem value="ja">日本語</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="timezone">시간대</Label>
                  <Select value={settings.timezone} onValueChange={(value) => setSettings({...settings, timezone: value})}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="Asia/Seoul">Asia/Seoul (KST)</SelectItem>
                      <SelectItem value="UTC">UTC</SelectItem>
                      <SelectItem value="America/New_York">America/New_York (EST)</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>기능 설정</CardTitle>
              <CardDescription>사이트의 주요 기능을 활성화/비활성화 합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>점검 모드</Label>
                  <p className="text-sm text-muted-foreground">
                    활성화하면 관리자를 제외한 모든 사용자의 접근이 차단됩니다.
                  </p>
                </div>
                <Switch
                  checked={settings.maintenanceMode}
                  onCheckedChange={(checked) => setSettings({...settings, maintenanceMode: checked})}
                />
              </div>
              
              <Separator />
              
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>사용자 가입 허용</Label>
                  <p className="text-sm text-muted-foreground">
                    새로운 사용자의 회원가입을 허용합니다.
                  </p>
                </div>
                <Switch
                  checked={settings.userRegistration}
                  onCheckedChange={(checked) => setSettings({...settings, userRegistration: checked})}
                />
              </div>
              
              <Separator />
              
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>이메일 알림</Label>
                  <p className="text-sm text-muted-foreground">
                    시스템 이벤트에 대한 이메일 알림을 전송합니다.
                  </p>
                </div>
                <Switch
                  checked={settings.emailNotifications}
                  onCheckedChange={(checked) => setSettings({...settings, emailNotifications: checked})}
                />
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="security" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <Shield className="h-5 w-5" />
                <span>보안 설정</span>
              </CardTitle>
              <CardDescription>시스템 보안 관련 설정을 관리합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="sessionTimeout">세션 만료시간 (분)</Label>
                  <Input
                    id="sessionTimeout"
                    type="number"
                    value={settings.sessionTimeout}
                    onChange={(e) => setSettings({...settings, sessionTimeout: parseInt(e.target.value)})}
                  />
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="maxFileSize">최대 파일 크기 (MB)</Label>
                  <Input
                    id="maxFileSize"
                    type="number"
                    value={settings.maxFileSize}
                    onChange={(e) => setSettings({...settings, maxFileSize: parseInt(e.target.value)})}
                  />
                </div>
              </div>

              <Separator />

              <div className="space-y-4">
                <h4 className="font-medium">비밀번호 정책</h4>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div className="space-y-2">
                    <Label htmlFor="minPasswordLength">최소 길이</Label>
                    <Input id="minPasswordLength" type="number" defaultValue="8" />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="passwordExpiry">비밀번호 만료 (일)</Label>
                    <Input id="passwordExpiry" type="number" defaultValue="90" />
                  </div>
                </div>
                <div className="flex items-center space-x-2">
                  <Switch id="requireSpecialChar" defaultChecked />
                  <Label htmlFor="requireSpecialChar">특수문자 포함 필수</Label>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>접근 제어</CardTitle>
              <CardDescription>IP 기반 접근 제어를 설정합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="allowedIPs">허용된 IP 주소</Label>
                <Textarea
                  id="allowedIPs"
                  placeholder="192.168.1.0/24&#10;10.0.0.0/8"
                  className="min-h-[100px]"
                />
                <p className="text-sm text-muted-foreground">
                  한 줄에 하나씩 입력하세요. CIDR 표기법을 사용할 수 있습니다.
                </p>
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="blockedIPs">차단된 IP 주소</Label>
                <Textarea
                  id="blockedIPs"
                  placeholder="203.0.113.0/24"
                  className="min-h-[100px]"
                />
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="email" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <Mail className="h-5 w-5" />
                <span>이메일 설정</span>
              </CardTitle>
              <CardDescription>시스템에서 사용할 이메일 서버를 설정합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="smtpHost">SMTP 서버</Label>
                  <Input id="smtpHost" placeholder="smtp.gmail.com" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="smtpPort">포트</Label>
                  <Input id="smtpPort" type="number" placeholder="587" />
                </div>
              </div>
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="smtpUsername">사용자명</Label>
                  <Input id="smtpUsername" placeholder="your-email@gmail.com" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="smtpPassword">비밀번호</Label>
                  <Input id="smtpPassword" type="password" placeholder="앱 비밀번호" />
                </div>
              </div>
              
              <div className="flex items-center space-x-2">
                <Switch id="enableTLS" defaultChecked />
                <Label htmlFor="enableTLS">TLS 암호화 사용</Label>
              </div>
              
              <Button variant="outline">
                <Mail className="h-4 w-4 mr-2" />
                테스트 이메일 전송
              </Button>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="system" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center space-x-2">
                <Database className="h-5 w-5" />
                <span>시스템 정보</span>
              </CardTitle>
              <CardDescription>현재 시스템의 상태와 정보를 확인합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              {systemInfo.map((info, index) => (
                <div key={index} className="flex items-center justify-between">
                  <span className="text-sm font-medium">{info.label}</span>
                  <div className="flex items-center space-x-2">
                    <span className="text-sm text-muted-foreground">{info.value}</span>
                    {info.progress && (
                      <div className="w-24">
                        <Progress value={info.progress} className="h-2" />
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>캐시 관리</CardTitle>
              <CardDescription>시스템 캐시를 관리합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">애플리케이션 캐시</p>
                  <p className="text-sm text-muted-foreground">마지막 정리: 2시간 전</p>
                </div>
                <Button variant="outline">
                  <RefreshCw className="h-4 w-4 mr-2" />
                  캐시 정리
                </Button>
              </div>
              
              <Separator />
              
              <div className="flex items-center justify-between">
                <div>
                  <p className="font-medium">데이터베이스 캐시</p>
                  <p className="text-sm text-muted-foreground">마지막 정리: 1일 전</p>
                </div>
                <Button variant="outline">
                  <RefreshCw className="h-4 w-4 mr-2" />
                  캐시 정리
                </Button>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="backup" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>백업 설정</CardTitle>
              <CardDescription>데이터 백업 설정을 관리합니다.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center justify-between">
                <div className="space-y-0.5">
                  <Label>자동 백업</Label>
                  <p className="text-sm text-muted-foreground">
                    매일 자정에 자동으로 백업을 생성합니다.
                  </p>
                </div>
                <Switch
                  checked={settings.autoBackup}
                  onCheckedChange={(checked) => setSettings({...settings, autoBackup: checked})}
                />
              </div>
              
              <Separator />
              
              <div className="space-y-2">
                <Label htmlFor="backupRetention">백업 보관 기간 (일)</Label>
                <Input id="backupRetention" type="number" defaultValue="30" />
              </div>
              
              <div className="flex space-x-2">
                <Button variant="outline">
                  <Download className="h-4 w-4 mr-2" />
                  즉시 백업
                </Button>
                <Button variant="outline">
                  <Upload className="h-4 w-4 mr-2" />
                  백업 복원
                </Button>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>백업 기록</CardTitle>
              <CardDescription>최근 백업 기록을 확인합니다.</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {[
                  { date: '2024-01-15 23:59', size: '234 MB', status: 'success' },
                  { date: '2024-01-14 23:59', size: '231 MB', status: 'success' },
                  { date: '2024-01-13 23:59', size: '228 MB', status: 'success' },
                  { date: '2024-01-12 23:59', size: '225 MB', status: 'failed' },
                ].map((backup, index) => (
                  <div key={index} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="flex items-center space-x-3">
                      <Badge
                        variant={backup.status === 'success' ? 'default' : 'destructive'}
                        className={backup.status === 'success' ? 'bg-green-100 text-green-800' : ''}
                      >
                        {backup.status === 'success' ? '성공' : '실패'}
                      </Badge>
                      <div>
                        <p className="text-sm font-medium">{backup.date}</p>
                        <p className="text-xs text-muted-foreground">{backup.size}</p>
                      </div>
                    </div>
                    <div className="flex space-x-2">
                      {backup.status === 'success' && (
                        <>
                          <Button variant="outline" size="sm">
                            <Download className="h-3 w-3" />
                          </Button>
                          <AlertDialog>
                            <AlertDialogTrigger asChild>
                              <Button variant="outline" size="sm">
                                <Trash2 className="h-3 w-3" />
                              </Button>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                              <AlertDialogHeader>
                                <AlertDialogTitle>백업 삭제</AlertDialogTitle>
                                <AlertDialogDescription>
                                  이 백업 파일을 삭제하시겠습니까? 이 작업은 취소할 수 없습니다.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel>취소</AlertDialogCancel>
                                <AlertDialogAction>삭제</AlertDialogAction>
                              </AlertDialogFooter>
                            </AlertDialogContent>
                          </AlertDialog>
                        </>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}