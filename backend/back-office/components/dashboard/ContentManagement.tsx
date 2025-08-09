import { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Badge } from '../ui/badge';
import { Textarea } from '../ui/textarea';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '../ui/table';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '../ui/select';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '../ui/dialog';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { Label } from '../ui/label';
import {
  Search,
  Plus,
  MoreVertical,
  Edit,
  Trash2,
  Eye,
  CheckCircle,
  Clock,
  XCircle,
  FileText,
} from 'lucide-react';

const mockContent = [
  {
    id: 1,
    title: '웹사이트 개편 안내',
    type: 'notice',
    author: '김철수',
    status: 'published',
    views: 1234,
    createdAt: '2024-01-15',
    updatedAt: '2024-01-15',
  },
  {
    id: 2,
    title: '새로운 기능 업데이트',
    type: 'news',
    author: '이영희',
    status: 'draft',
    views: 0,
    createdAt: '2024-01-14',
    updatedAt: '2024-01-14',
  },
  {
    id: 3,
    title: '시스템 점검 예정 공지',
    type: 'notice',
    author: '박민수',
    status: 'pending',
    views: 567,
    createdAt: '2024-01-13',
    updatedAt: '2024-01-14',
  },
  {
    id: 4,
    title: 'FAQ 업데이트',
    type: 'faq',
    author: '최정훈',
    status: 'published',
    views: 890,
    createdAt: '2024-01-12',
    updatedAt: '2024-01-13',
  },
];

export function ContentManagement() {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [typeFilter, setTypeFilter] = useState('all');
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  const filteredContent = mockContent.filter((item) => {
    const matchesSearch = item.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         item.author.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'all' || item.status === statusFilter;
    const matchesType = typeFilter === 'all' || item.type === typeFilter;
    
    return matchesSearch && matchesStatus && matchesType;
  });

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'published':
        return (
          <Badge variant="default" className="bg-green-100 text-green-800">
            <CheckCircle className="h-3 w-3 mr-1" />
            게시됨
          </Badge>
        );
      case 'draft':
        return (
          <Badge variant="secondary">
            <Edit className="h-3 w-3 mr-1" />
            초안
          </Badge>
        );
      case 'pending':
        return (
          <Badge variant="outline" className="border-orange-300 text-orange-600">
            <Clock className="h-3 w-3 mr-1" />
            검토 중
          </Badge>
        );
      case 'rejected':
        return (
          <Badge variant="destructive">
            <XCircle className="h-3 w-3 mr-1" />
            거절됨
          </Badge>
        );
      default:
        return <Badge variant="secondary">{status}</Badge>;
    }
  };

  const getTypeBadge = (type: string) => {
    switch (type) {
      case 'notice':
        return <Badge variant="default">공지사항</Badge>;
      case 'news':
        return <Badge variant="default" className="bg-blue-100 text-blue-800">뉴스</Badge>;
      case 'faq':
        return <Badge variant="outline">FAQ</Badge>;
      case 'blog':
        return <Badge variant="secondary">블로그</Badge>;
      default:
        return <Badge variant="secondary">{type}</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      {/* 상단 통계 */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center space-x-2">
              <CheckCircle className="h-4 w-4 text-green-600" />
              <div>
                <p className="text-2xl font-bold">
                  {mockContent.filter(c => c.status === 'published').length}
                </p>
                <p className="text-sm text-muted-foreground">게시된 콘텐츠</p>
              </div>
            </div>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center space-x-2">
              <Edit className="h-4 w-4 text-gray-600" />
              <div>
                <p className="text-2xl font-bold">
                  {mockContent.filter(c => c.status === 'draft').length}
                </p>
                <p className="text-sm text-muted-foreground">초안</p>
              </div>
            </div>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center space-x-2">
              <Clock className="h-4 w-4 text-orange-600" />
              <div>
                <p className="text-2xl font-bold">
                  {mockContent.filter(c => c.status === 'pending').length}
                </p>
                <p className="text-sm text-muted-foreground">검토 대기</p>
              </div>
            </div>
          </CardContent>
        </Card>
        
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center space-x-2">
              <Eye className="h-4 w-4 text-blue-600" />
              <div>
                <p className="text-2xl font-bold">
                  {mockContent.reduce((sum, c) => sum + c.views, 0).toLocaleString()}
                </p>
                <p className="text-sm text-muted-foreground">총 조회수</p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 콘텐츠 목록 */}
      <Card>
        <CardHeader>
          <div className="flex flex-col md:flex-row md:items-center md:justify-between space-y-4 md:space-y-0">
            <div>
              <CardTitle>콘텐츠 목록</CardTitle>
              <CardDescription>시스템의 모든 콘텐츠를 관리합니다.</CardDescription>
            </div>
            <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
              <DialogTrigger asChild>
                <Button>
                  <Plus className="h-4 w-4 mr-2" />
                  새 콘텐츠 생성
                </Button>
              </DialogTrigger>
              <DialogContent className="sm:max-w-[425px]">
                <DialogHeader>
                  <DialogTitle>새 콘텐츠 생성</DialogTitle>
                  <DialogDescription>
                    새로운 콘텐츠를 작성합니다.
                  </DialogDescription>
                </DialogHeader>
                <div className="grid gap-4 py-4">
                  <div className="grid gap-2">
                    <Label htmlFor="title">제목</Label>
                    <Input id="title" placeholder="콘텐츠 제목을 입력하세요" />
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="type">유형</Label>
                    <Select>
                      <SelectTrigger>
                        <SelectValue placeholder="콘텐츠 유형 선택" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="notice">공지사항</SelectItem>
                        <SelectItem value="news">뉴스</SelectItem>
                        <SelectItem value="faq">FAQ</SelectItem>
                        <SelectItem value="blog">블로그</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                  <div className="grid gap-2">
                    <Label htmlFor="content">내용</Label>
                    <Textarea 
                      id="content" 
                      placeholder="콘텐츠 내용을 입력하세요" 
                      className="min-h-[100px]"
                    />
                  </div>
                </div>
                <DialogFooter>
                  <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                    취소
                  </Button>
                  <Button onClick={() => setIsCreateDialogOpen(false)}>
                    저장
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          </div>
        </CardHeader>
        
        <CardContent>
          {/* 필터 및 검색 */}
          <div className="flex flex-col md:flex-row gap-4 mb-6">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="제목 또는 작성자로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-9"
              />
            </div>
            
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-full md:w-32">
                <SelectValue placeholder="상태" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">모든 상태</SelectItem>
                <SelectItem value="published">게시됨</SelectItem>
                <SelectItem value="draft">초안</SelectItem>
                <SelectItem value="pending">검토 중</SelectItem>
              </SelectContent>
            </Select>
            
            <Select value={typeFilter} onValueChange={setTypeFilter}>
              <SelectTrigger className="w-full md:w-32">
                <SelectValue placeholder="유형" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">모든 유형</SelectItem>
                <SelectItem value="notice">공지사항</SelectItem>
                <SelectItem value="news">뉴스</SelectItem>
                <SelectItem value="faq">FAQ</SelectItem>
                <SelectItem value="blog">블로그</SelectItem>
              </SelectContent>
            </Select>
          </div>

          {/* 콘텐츠 테이블 */}
          <div className="border rounded-lg">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>제목</TableHead>
                  <TableHead>유형</TableHead>
                  <TableHead>작성자</TableHead>
                  <TableHead>상태</TableHead>
                  <TableHead>조회수</TableHead>
                  <TableHead>수정일</TableHead>
                  <TableHead className="text-right">작업</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {filteredContent.map((content) => (
                  <TableRow key={content.id}>
                    <TableCell className="font-medium">
                      <div className="flex items-center space-x-2">
                        <FileText className="h-4 w-4 text-muted-foreground" />
                        <span>{content.title}</span>
                      </div>
                    </TableCell>
                    <TableCell>{getTypeBadge(content.type)}</TableCell>
                    <TableCell className="text-muted-foreground">{content.author}</TableCell>
                    <TableCell>{getStatusBadge(content.status)}</TableCell>
                    <TableCell className="text-muted-foreground">
                      {content.views.toLocaleString()}
                    </TableCell>
                    <TableCell className="text-sm text-muted-foreground">
                      {content.updatedAt}
                    </TableCell>
                    <TableCell className="text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="sm">
                            <MoreVertical className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem>
                            <Eye className="h-4 w-4 mr-2" />
                            미리보기
                          </DropdownMenuItem>
                          <DropdownMenuItem>
                            <Edit className="h-4 w-4 mr-2" />
                            편집
                          </DropdownMenuItem>
                          <DropdownMenuSeparator />
                          <DropdownMenuItem className="text-red-600">
                            <Trash2 className="h-4 w-4 mr-2" />
                            삭제
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          {filteredContent.length === 0 && (
            <div className="text-center py-8">
              <p className="text-muted-foreground">검색 조건에 맞는 콘텐츠가 없습니다.</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}