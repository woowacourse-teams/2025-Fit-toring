import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";

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

import {
  fetchMentorings,
  MentoringSummary,
} from "../../services/mentoringApi";

export function MentoringManagement() {
  const navigate = useNavigate();

  const [mentorings, setMentorings] = useState<MentoringSummary[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // -------------------------------------
  // 목록 로딩
  // -------------------------------------
  const loadMentorings = async (page: number = 1) => {
    try {
      setIsLoading(true);

      const res = await fetchMentorings(page, 10);

      const normalized = res.items.map((m) => ({
        ...m,
        id: Number(m.id), // 반드시 number
      }));

      setMentorings(normalized);
      setTotalPages(res.totalPages);
      setTotalElements(res.totalElements);
    } catch (err) {
      console.error(err);
      setError("멘토링 목록 조회 실패");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadMentorings(currentPage);
  }, [currentPage]);

  // -------------------------------------
  // 상세 페이지 이동
  // -------------------------------------
  const handleViewDetail = (id: number) => {
    navigate(ROUTES.getMentoringDetailPath(String(id)));
  };

  // -------------------------------------
  // 페이지네이션
  // -------------------------------------
  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const maxPagesToShow = 5;
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1)
      startPage = Math.max(1, endPage - maxPagesToShow + 1);

    const pages = [];
    for (let i = startPage; i <= endPage; i++) pages.push(i);

    return (
      <div className="flex justify-end py-4 px-2">
        <Pagination>
          <PaginationContent>

            {/* Prev */}
            <PaginationItem>
              <PaginationPrevious
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  if (currentPage > 1) setCurrentPage(currentPage - 1);
                }}
                className={currentPage === 1 ? "pointer-events-none opacity-50" : ""}
              />
            </PaginationItem>

            {/* 1 ... */}
            {startPage > 1 && (
              <>
                <PaginationItem>
                  <PaginationLink
                    href="#"
                    isActive={currentPage === 1}
                    data-state={currentPage === 1 ? "active" : ""}
                    onClick={(e) => {
                      e.preventDefault();
                      setCurrentPage(1);
                    }}
                  >
                    1
                  </PaginationLink>
                </PaginationItem>
                {startPage > 2 && <PaginationEllipsis />}
              </>
            )}

            {/* middle pages */}
            {pages.map((p) => (
              <PaginationItem key={p}>
                <PaginationLink
                  href="#"
                  isActive={currentPage === p}
                  data-state={currentPage === p ? "active" : ""}
                  onClick={(e) => {
                    e.preventDefault();
                    setCurrentPage(p);
                  }}
                >
                  {p}
                </PaginationLink>
              </PaginationItem>
            ))}

            {/* ... last */}
            {endPage < totalPages && (
              <>
                {endPage < totalPages - 1 && <PaginationEllipsis />}
                <PaginationItem>
                  <PaginationLink
                    href="#"
                    isActive={currentPage === totalPages}
                    data-state={currentPage === totalPages ? "active" : ""}
                    onClick={(e) => {
                      e.preventDefault();
                      setCurrentPage(totalPages);
                    }}
                  >
                    {totalPages}
                  </PaginationLink>
                </PaginationItem>
              </>
            )}

            {/* Next */}
            <PaginationItem>
              <PaginationNext
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  if (currentPage < totalPages)
                    setCurrentPage(currentPage + 1);
                }}
                className={currentPage === totalPages ? "pointer-events-none opacity-50" : ""}
              />
            </PaginationItem>

          </PaginationContent>
        </Pagination>
      </div>
    );
  };

  // -------------------------------------
  // UI
  // -------------------------------------
  return (
    <div className="space-y-6">

      {/* 헤더 */}
      <div>
        <h2 className="text-xl font-semibold">멘토링 관리</h2>
        <p className="text-muted-foreground">
          {!isLoading && (
            <span className="ml-2">{totalElements}건</span>
          )}
        </p>
      </div>

      {/* 목록 테이블 */}
      <div className="border rounded-md">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="pl-8">ID</TableHead>
              <TableHead>멘토명</TableHead>
              <TableHead>카테고리</TableHead>
              <TableHead>가격</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {isLoading ? (
              <TableRow>
                <TableCell colSpan={4} className="py-6 text-center">
                  로딩 중...
                </TableCell>
              </TableRow>
            ) : error ? (
              <TableRow>
                <TableCell colSpan={4} className="py-6 text-center text-red-500">
                  {error}
                </TableCell>
              </TableRow>
            ) : (
              mentorings.map((m) => (
                <TableRow
                  key={m.id}
                  className="cursor-pointer hover:bg-muted/50"
                  onClick={() => handleViewDetail(m.id)}
                >
                  <TableCell className="pl-8">{m.id}</TableCell>
                  <TableCell>{m.mentorName}</TableCell>
                  <TableCell>
                    <div className="flex gap-1 flex-wrap">
                      {m.categories.slice(0, 3).map((cat, i) => (
                        <Badge key={i} variant="outline">{cat}</Badge>
                      ))}
                      {m.categories.length > 3 && (
                        <Badge variant="outline" className="bg-gray-100 text-gray-600">
                          +{m.categories.length - 3}
                        </Badge>
                      )}
                    </div>
                  </TableCell>
                  <TableCell>
                    {new Intl.NumberFormat("ko-KR").format(m.price)}원
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