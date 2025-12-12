import { useEffect, useState } from "react";
import { toast } from "sonner";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { Button } from "../ui/button";
import { Badge } from "../ui/badge";
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
} from "../ui/alert-dialog";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "../ui/pagination";
import { MessageSquare, Star, Trash2 } from "lucide-react";
import { formatDateTime } from "@/utils/Formatter";
import {
  fetchMentoringReviews,
  MentoringReviewListResponse,
  MentoringReview,
  deleteReview,
} from "@/services/reviewApi";

interface MentoringReviewProps {
  mentoringId: number;
}

export function MentoringReviewSection({ mentoringId }: MentoringReviewProps) {
  const [reviews, setReviews] = useState<MentoringReviewListResponse | null>(
    null,
  );
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // 페이지네이션(프론트 기준)
  const [currentPage, setCurrentPage] = useState<number>(1);
  const pageSize = 5;

  useEffect(() => {
    const load = async () => {
      try {
        setIsLoading(true);
        setError(null);
        setCurrentPage(1); // 멘토링 ID 변경 시 1페이지로 리셋

        const data = await fetchMentoringReviews(mentoringId);
        setReviews(data);
      } catch (e) {
        console.error("멘토링 리뷰 조회 실패:", e);
        setError("리뷰 데이터를 불러오지 못했습니다.");
        setReviews(null);
      } finally {
        setIsLoading(false);
      }
    };

    if (mentoringId && !Number.isNaN(mentoringId)) {
      load();
    }
  }, [mentoringId]);

  const handleDeleteReview = async (reviewId: number) => {
    if (!reviews) return;
  
    // 롤백 대비 백업
    const prevReviews = reviews;
    const prevPage = currentPage;
  
    // 1) UI에서 먼저 제거 (낙관적 업데이트)
    const remaining = reviews.reviewData.filter((r) => r.id !== reviewId);
    const ratingCount = remaining.length;
  
    let ratingAverage = "0";
    if (ratingCount > 0) {
      const total = remaining.reduce(
        (sum, r) => sum + Number(r.rating ?? 0),
        0,
      );
      ratingAverage = (total / ratingCount).toFixed(1);
    }
  
    setReviews({
      ratingAverage,
      ratingCount,
      reviewData: remaining,
    });
  
    // 페이지 자동 보정
    const totalPages = Math.max(1, Math.ceil(ratingCount / pageSize));
    if (currentPage > totalPages) {
      setCurrentPage(totalPages);
    }
  
    // 2) 실제 API 호출
    try {
      await deleteReview(reviewId);
      toast.success("리뷰가 삭제되었습니다.");
    } catch (e) {
      console.error("리뷰 삭제 실패:", e);
  
      // 3) 실패 → rollback
      setReviews(prevReviews);
      setCurrentPage(prevPage);
  
      toast.error("리뷰 삭제 중 오류가 발생했습니다.");
    }
  };

  const renderStars = (rating: number) =>
    Array.from({ length: 5 }, (_, idx) => (
      <Star
        key={idx}
        className={
          idx < rating
            ? "h-4 w-4 text-yellow-400 fill-yellow-400"
            : "h-4 w-4 text-gray-300"
        }
      />
    ));

  // 페이지네이션 계산(프론트 기준)
  const totalReviewCount = reviews?.reviewData.length ?? 0;
  const totalPages =
    totalReviewCount === 0 ? 1 : Math.ceil(totalReviewCount / pageSize);

  const startIdx = (currentPage - 1) * pageSize;
  const endIdx = startIdx + pageSize;
  const currentPageReviews: MentoringReview[] =
    reviews?.reviewData.slice(startIdx, endIdx) ?? [];

  const renderPagination = () => {
    if (!reviews || totalReviewCount === 0) return null;
    if (totalPages <= 1) return null;

    const pageNumbers: number[] = [];
    const maxPagesToShow = 5;
    let startPage: number;
    let endPage: number;

    if (totalPages <= maxPagesToShow) {
      startPage = 1;
      endPage = totalPages;
    } else {
      const maxPagesBeforeCurrent = Math.floor(maxPagesToShow / 2);
      const maxPagesAfterCurrent = Math.ceil(maxPagesToShow / 2) - 1;

      if (currentPage <= maxPagesBeforeCurrent) {
        startPage = 1;
        endPage = maxPagesToShow;
      } else if (currentPage + maxPagesAfterCurrent >= totalPages) {
        startPage = totalPages - maxPagesToShow + 1;
        endPage = totalPages;
      } else {
        startPage = currentPage - maxPagesBeforeCurrent;
        endPage = currentPage + maxPagesAfterCurrent;
      }
    }

    for (let i = startPage; i <= endPage; i++) {
      pageNumbers.push(i);
    }

    const handlePageChange = (page: number) => {
      if (page >= 1 && page <= totalPages) {
        setCurrentPage(page);
      }
    };

    return (
      <div className="flex items-center justify-end py-4 px-6">
        <Pagination>
          <PaginationContent>
            {/* 이전 */}
            <PaginationItem>
              <PaginationPrevious
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  handlePageChange(currentPage - 1);
                }}
                className={
                  currentPage === 1
                    ? "pointer-events-none opacity-50"
                    : ""
                }
              />
            </PaginationItem>

            {/* 처음 + ... */}
            {startPage > 1 && (
              <>
                <PaginationItem>
                  <PaginationLink
                    href="#"
                    onClick={(e) => {
                      e.preventDefault();
                      handlePageChange(1);
                    }}
                  >
                    1
                  </PaginationLink>
                </PaginationItem>
                {startPage > 2 && (
                  <PaginationItem>
                    <PaginationEllipsis />
                  </PaginationItem>
                )}
              </>
            )}

            {/* 가운데 페이지 */}
            {pageNumbers.map((number) => (
              <PaginationItem key={number}>
                <PaginationLink
                  href="#"
                  onClick={(e) => {
                    e.preventDefault();
                    handlePageChange(number);
                  }}
                  isActive={currentPage === number}
                >
                  {number}
                </PaginationLink>
              </PaginationItem>
            ))}

            {/* ... + 마지막 */}
            {endPage < totalPages && (
              <>
                {endPage < totalPages - 1 && (
                  <PaginationItem>
                    <PaginationEllipsis />
                  </PaginationItem>
                )}
                <PaginationItem>
                  <PaginationLink
                    href="#"
                    onClick={(e) => {
                      e.preventDefault();
                      handlePageChange(totalPages);
                    }}
                  >
                    {totalPages}
                  </PaginationLink>
                </PaginationItem>
              </>
            )}

            {/* 다음 */}
            <PaginationItem>
              <PaginationNext
                href="#"
                onClick={(e) => {
                  e.preventDefault();
                  handlePageChange(currentPage + 1);
                }}
                className={
                  currentPage === totalPages
                    ? "pointer-events-none opacity-50"
                    : ""
                }
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      </div>
    );
  };

  return (
    <Card>
      <CardHeader>
        <div className="flex justify-between items-start">
          <div>
            <CardTitle className="flex items-center gap-2">
              <MessageSquare className="h-5 w-5" />
              리뷰 목록
            </CardTitle>
            <CardDescription>
              {isLoading && "리뷰 데이터를 불러오는 중입니다..."}
              {error && <span className="text-red-500">{error}</span>}
              {!isLoading && !error && reviews && (
                <div className="flex items-center gap-2 mt-2">
                  <div className="flex items-center gap-1">
                    {renderStars(
                      Math.round(parseFloat(reviews.ratingAverage || "0")),
                    )}
                    <span className="ml-2 font-medium">
                      {reviews.ratingAverage}
                    </span>
                  </div>
                  <span className="text-muted-foreground">
                    (총 {reviews.ratingCount}건)
                  </span>
                </div>
              )}
              {!isLoading && !error && !reviews && "아직 리뷰가 없습니다."}
            </CardDescription>
          </div>
        </div>
      </CardHeader>

      <CardContent>
        {!reviews || reviews.reviewData.length === 0 ? (
          <div className="text-center py-8">
            <p className="text-muted-foreground mb-4">
              등록된 리뷰가 없습니다.
            </p>
          </div>
        ) : (
          <>
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="pl-8">리뷰 ID</TableHead>
                    <TableHead>멘티 이름</TableHead>
                    <TableHead>평점</TableHead>
                    <TableHead>리뷰 내용</TableHead>
                    <TableHead>작성일</TableHead>
                    <TableHead className="text-right pr-8">
                      삭제
                    </TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {currentPageReviews.map((review) => (
                    <TableRow key={review.id}>
                      <TableCell className="font-medium pl-8 py-6">
                        {review.id}
                      </TableCell>
                      <TableCell className="py-3">
                        {review.menteeName} ({review.menteeId})
                      </TableCell>
                      <TableCell className="py-3">
                        <div className="flex items-center gap-1">
                          {renderStars(review.rating)}
                          <span className="ml-2 font-medium text-sm">
                            ({review.rating}점)
                          </span>
                        </div>
                      </TableCell>
                      <TableCell className="py-3">
                        <div className="w-80 overflow-x-auto scrollbar-thin px-3 py-2 bg-muted/30 rounded-md border">
                          <p className="text-sm text-muted-foreground whitespace-nowrap leading-relaxed min-w-0">
                            {review.content}
                          </p>
                        </div>
                      </TableCell>
                      <TableCell className="py-3">
                        {formatDateTime(review.createdAt)}
                      </TableCell>
                      <TableCell className="py-3 pr-8">
                        <div className="flex justify-end">
                          <AlertDialog>
                            <AlertDialogTrigger asChild>
                              <Button
                                variant="ghost"
                                size="sm"
                                className="text-red-600 hover:text-red-700 hover:bg-red-50"
                              >
                                <Trash2 className="h-4 w-4 mr-2" />
                                삭제
                              </Button>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                              <AlertDialogHeader>
                                <AlertDialogTitle>
                                  리뷰 삭제 확인
                                </AlertDialogTitle>
                                <AlertDialogDescription>
                                  정말로 이 리뷰를 삭제하시겠습니까?
                                  <br />
                                  <strong>
                                    {review.menteeName}(
                                    {review.menteeId})
                                  </strong>
                                  님의 리뷰가 영구적으로 삭제됩니다.
                                  <br />
                                  이 작업은 되돌릴 수 없습니다.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel>
                                  취소
                                </AlertDialogCancel>
                                <AlertDialogAction
                                  onClick={() =>
                                    handleDeleteReview(review.id)
                                  }
                                  className="bg-red-600 hover:bg-red-700"
                                >
                                  삭제
                                </AlertDialogAction>
                              </AlertDialogFooter>
                            </AlertDialogContent>
                          </AlertDialog>
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
            {renderPagination()}
          </>
        )}
      </CardContent>
    </Card>
  );
}
