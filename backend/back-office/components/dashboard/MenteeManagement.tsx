import { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { Badge } from "../ui/badge";

// 사용자 데이터 타입
interface UserItem {
  id: number;
  name: string;
  loginId: string;
  gender: string;
  phoneNumber: string;
  role: "MENTOR" | "MENTEE" | "ADMIN";
}

// 더미 데이터
const mockUserData: UserItem[] = [
  {
    id: 1,
    name: "김민수",
    loginId: "minsu123",
    gender: "남성",
    phoneNumber: "010-1234-5678",
    role: "MENTEE",
  },
  {
    id: 2,
    name: "박지영",
    loginId: "jiyoung456",
    gender: "여성",
    phoneNumber: "010-2345-6789",
    role: "MENTEE",
  },
  {
    id: 3,
    name: "이성훈",
    loginId: "seonghun789",
    gender: "남성",
    phoneNumber: "010-3456-7890",
    role: "MENTOR",
  },
  {
    id: 4,
    name: "최수진",
    loginId: "sujin012",
    gender: "여성",
    phoneNumber: "010-4567-8901",
    role: "MENTEE",
  },
  {
    id: 5,
    name: "정태현",
    loginId: "taehyun345",
    gender: "남성",
    phoneNumber: "010-5678-9012",
    role: "MENTOR",
  },
  {
    id: 6,
    name: "관리자",
    loginId: "admin",
    gender: "남성",
    phoneNumber: "010-0000-0000",
    role: "ADMIN",
  },
];

// 역할별 색상 매핑
const roleColors = {
  MENTOR: "bg-blue-100 text-blue-800",
  MENTEE: "bg-green-100 text-green-800",
  ADMIN: "bg-purple-100 text-purple-800",
};

// 역할별 한글명
const roleLabels = {
  MENTOR: "멘토",
  MENTEE: "멘티",
  ADMIN: "관리자",
};

export function MenteeManagement() {
  const [selectedRole, setSelectedRole] = useState<string>("ALL");

  // 역할별 필터링
  const filteredUsers = selectedRole === "ALL" 
    ? mockUserData 
    : mockUserData.filter(user => user.role === selectedRole);

  const handleViewDetail = (userId: number) => {
    // TODO: 사용자 상세 페이지로 이동
    console.log("사용자 상세 보기:", userId);
  };

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex justify-between items-center">
        <div>
          <h2>멘티 관리</h2>
          <p className="text-muted-foreground">
            등록된 사용자를 조회할 수 있습니다.
          </p>
        </div>
      </div>
      {/* 사용자 목록 테이블 */}
      <div className="space-y-4">
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="pl-8">
                  사용자 ID
                </TableHead>
                <TableHead>이름</TableHead>
                <TableHead>로그인 ID</TableHead>
                <TableHead>성별</TableHead>
                <TableHead>전화번호</TableHead>
                <TableHead>역할</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredUsers.map((user) => (
                <TableRow
                  key={user.id}
                  className="cursor-pointer hover:bg-muted/50"
                  onClick={() => handleViewDetail(user.id)}
                >
                  <TableCell className="font-medium pl-8 py-3">
                    {user.id}
                  </TableCell>
                  <TableCell className="py-3">
                    {user.name}
                  </TableCell>
                  <TableCell className="py-3">
                    {user.loginId}
                  </TableCell>
                  <TableCell className="py-3">
                    {user.gender}
                  </TableCell>
                  <TableCell className="py-3">
                    {user.phoneNumber}
                  </TableCell>
                  <TableCell className="py-3">
                    <Badge
                      variant="outline"
                      className={roleColors[user.role]}
                    >
                      {roleLabels[user.role]}
                    </Badge>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
        
        {/* 검색 결과가 없을 때 */}
        {filteredUsers.length === 0 && (
          <div className="text-center py-8 text-muted-foreground">
            해당 조건에 맞는 사용자가 없습니다.
          </div>
        )}
      </div>
    </div>
  );
}
