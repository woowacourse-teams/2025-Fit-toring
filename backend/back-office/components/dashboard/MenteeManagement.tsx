import { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { Badge } from "../ui/badge";

import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
  PaginationEllipsis,
} from "../ui/pagination";

import { fetchMembers, MemberItem } from "@/services/memberApi";

// 역할별 색상
const roleColors = {
  MENTOR: "bg-blue-100 text-blue-800",
  MENTEE: "bg-green-100 text-green-800",
  ADMIN: "bg-purple-100 text-purple-800",
};

const roleLabels = {
  MENTOR: "멘토",
  MENTEE: "멘티",
  ADMIN: "관리자",
};

export function MenteeManagement() {
  const [members, setMembers] = useState<MemberItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const [currentPage, setCurrentPage] = useState(1); // 1-based
  const pageSize = 20;

  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  useEffect(() => {
    const load = async () => {
      setIsLoading(true);
      try {
        // 서버는 page=1 이 첫페이지라 그대로 사용
        const data = await fetchMembers(currentPage, pageSize);

        setMembers(data.content);
        setTotalPages(data.totalPages);
        setTotalElements(data.total);
      } catch (err) {
        console.error("❌ 사용자 목록 조회 실패:", err);
      } finally {
        setIsLoading(false);
      }
    };

    load();
  }, [currentPage]);

  const handlePageChange = (page: number) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  // Pagination rendering
  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const pagesToShow = 5;
    const start = Math.max(1, currentPage - 2);
    const end = Math.min(totalPages, start + pagesToShow - 1);

    return (
      <div className="flex justify-end py-4 px-6">
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  handlePageChange(currentPage - 1);
                }}
                className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
              />
            </PaginationItem>

            {start > 1 && (
              <>
                <PaginationItem>
                  <PaginationLink href="#" onClick={() => handlePageChange(1)}>1</PaginationLink>
                </PaginationItem>
                {start > 2 && <PaginationEllipsis />}
              </>
            )}

            {Array.from({ length: end - start + 1 }, (_, i) => start + i).map((num) => (
              <PaginationItem key={num}>
                <PaginationLink
                  href="#"
                  onClick={(e) => {
                    e.preventDefault();
                    handlePageChange(num);
                  }}
                  isActive={currentPage === num}
                >
                  {num}
                </PaginationLink>
              </PaginationItem>
            ))}

            {end < totalPages && (
              <>
                {end < totalPages - 1 && <PaginationEllipsis />}
                <PaginationItem>
                  <PaginationLink href="#" onClick={() => handlePageChange(totalPages)}>
                    {totalPages}
                  </PaginationLink>
                </PaginationItem>
              </>
            )}

            <PaginationItem>
              <PaginationNext
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  handlePageChange(currentPage + 1);
                }}
                className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </div>
    );
  };

  const handleViewDetail = (id: number) => {
    console.log("상세 이동:", id);
  };

  return (
    <div className="space-y-6">
      {/* 헤더 */}
      <div className="flex justify-between items-center">
        <div>
          <h2>멘티 관리</h2>
          <p className="text-muted-foreground">등록된 사용자를 조회할 수 있습니다. (총 {totalElements}명)</p>
        </div>
      </div>

      {/* 테이블 */}
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="pl-8">로그인 ID</TableHead>
              <TableHead>이름</TableHead>
              <TableHead>성별</TableHead>
              <TableHead>전화번호</TableHead>
              <TableHead>역할</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {isLoading ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-10">
                  불러오는 중...
                </TableCell>
              </TableRow>
            ) : members.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-10 text-muted-foreground">
                  표시할 사용자가 없습니다.
                </TableCell>
              </TableRow>
            ) : (
              members.map((user, idx) => (
                <TableRow key={`${currentPage}-${idx}`} onClick={() => handleViewDetail(idx)}>
                  <TableCell className="font-medium pl-8">{user.loginId}</TableCell>
                  <TableCell>{user.name}</TableCell>
                  <TableCell>{user.gender}</TableCell>
                  <TableCell>{user.phoneNumber}</TableCell>
                  <TableCell>
                    <Badge variant="outline" className={roleColors[user.role]}>
                      {roleLabels[user.role]}
                    </Badge>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      {renderPagination()}
    </div>
  );
}
