import { useEffect, useState } from "react";
import {
  Card,
  CardHeader,
  CardContent,
  CardDescription,
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
import { Select, SelectTrigger, SelectContent, SelectItem, SelectValue } from "../ui/select";
import { Plus, Trash2, Clock } from "lucide-react";

import { formatDateTime } from "@/utils/Formatter";

import {
  fetchReservations,
  Reservation,
  fetchUpdateStatusReservation,
  fetchDeleteReservation,
} from "@/services/reservationApi";

interface MentoringReservationProps {
  mentoringId: number;
}

export function MentoringReservationSection({ mentoringId }: MentoringReservationProps) {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // 서버 페이지네이션 값
  const pageSize = 5;
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [totalElements, setTotalElements] = useState<number>(0);

  const [tempStatus, setTempStatus] = useState<
    Record<number, Reservation["status"]>
  >({});
  const [rowBusy, setRowBusy] = useState<Record<number, boolean>>({});

  // 서버에서 예약 목록 로드
  useEffect(() => {
    const load = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const data = await fetchReservations(mentoringId, currentPage, pageSize);

        setReservations(data.items);
        setTotalPages(data.totalPages);
        setTotalElements(data.total);
      } catch (e) {
        console.error("예약 목록 조회 실패:", e);
        setError("예약 데이터를 불러오지 못했습니다.");
        setReservations([]);
      } finally {
        setIsLoading(false);
      }
    };

    if (mentoringId) load();
  }, [mentoringId, currentPage]);

  const handleAddReservation = () => {
    alert("예약 추가 기능은 추후 구현 예정입니다.");
  };

  const handleStatusChange = (
    reservationId: number,
    newStatus: Reservation["status"]
  ) => {
    setTempStatus((prev) => ({ ...prev, [reservationId]: newStatus }));
  };

  const handleStatusUpdate = async (reservationId: number) => {
    const newStatus = tempStatus[reservationId];
    if (!newStatus) return;

    const prevReservations = reservations;

    try {
      setRowBusy((p) => ({ ...p, [reservationId]: true }));

      setReservations((prev) =>
        prev.map((r) =>
          r.id === reservationId ? { ...r, status: newStatus } : r
        )
      );

      await fetchUpdateStatusReservation(reservationId, newStatus);

      setTempStatus((prev) => {
        const c = { ...prev };
        delete c[reservationId];
        return c;
      });
    } catch (e) {
      console.error("예약 상태 수정 실패:", e);
      setReservations(prevReservations);
    } finally {
      setRowBusy((p) => ({ ...p, [reservationId]: false }));
    }
  };

  const handleDeleteReservation = async (reservationId: number) => {
    const prevReservations = reservations;

    try {
      setRowBusy((p) => ({ ...p, [reservationId]: true }));

      // UI 먼저 반영
      setReservations((prev) => prev.filter((r) => r.id !== reservationId));

      await fetchDeleteReservation(reservationId);
    } catch (e) {
      console.error("예약 삭제 실패:", e);
      setReservations(prevReservations);
    } finally {
      setRowBusy((p) => ({ ...p, [reservationId]: false }));
    }
  };

  const getStatusBadge = (status: Reservation["status"]) => {
    switch (status) {
      case "PENDING":
        return <Badge className="bg-yellow-100 text-yellow-800" variant="outline">대기중</Badge>;
      case "APPROVED":
        return <Badge className="bg-green-100 text-green-800" variant="outline">승인됨</Badge>;
      case "REJECTED":
        return <Badge className="bg-red-100 text-red-800" variant="outline">거절됨</Badge>;
      case "COMPLETE":
        return <Badge className="bg-blue-100 text-blue-800" variant="outline">완료</Badge>;
      default:
        return <Badge variant="outline">알 수 없음</Badge>;
    }
  };

  /** Pagination 렌더링 */
  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const maxPagesToShow = 5;
    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(1, endPage - maxPagesToShow + 1);
    }

    const pages = [];
    for (let p = startPage; p <= endPage; p++) pages.push(p);

    return (
      <div className="flex justify-end py-4">
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                href="#"
                className={currentPage === 1 ? "opacity-50 pointer-events-none" : ""}
                onClick={(e) => {
                  e.preventDefault();
                  if (currentPage > 1) setCurrentPage(currentPage - 1);
                }}
              />
            </PaginationItem>

            {pages.map((p) => (
              <PaginationItem key={p}>
                <PaginationLink
                  href="#"
                  isActive={p === currentPage}
                  onClick={(e) => {
                    e.preventDefault();
                    setCurrentPage(p);
                  }}
                >
                  {p}
                </PaginationLink>
              </PaginationItem>
            ))}

            <PaginationItem>
              <PaginationNext
                href="#"
                className={currentPage === totalPages ? "opacity-50 pointer-events-none" : ""}
                onClick={(e) => {
                  e.preventDefault();
                  if (currentPage < totalPages) setCurrentPage(currentPage + 1);
                }}
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
              <Clock className="h-5 w-5" />
              예약 목록
            </CardTitle>

            <CardDescription>
              {isLoading && "예약 데이터를 불러오는 중입니다..."}
              {error && <span className="text-red-500">{error}</span>}
              {!isLoading && !error && <>총 {totalElements}건</>}
            </CardDescription>
          </div>

          <Button onClick={handleAddReservation} size="sm">
            <Plus className="h-4 w-4 mr-2" />
            예약 추가
          </Button>
        </div>
      </CardHeader>

      <CardContent>
        {reservations.length === 0 ? (
          <div className="text-center py-8 text-muted-foreground">
            예약된 멘토링이 없습니다.
          </div>
        ) : (
          <>
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="pl-8">멘티 이름</TableHead>
                    <TableHead className="pl-8">신청문구</TableHead>
                    <TableHead>요청 시간</TableHead>
                    <TableHead>현재 상태</TableHead>
                    <TableHead>수정</TableHead>
                    <TableHead className="text-right pr-8">삭제</TableHead>
                  </TableRow>
                </TableHeader>

                <TableBody>
                  {reservations.map((reservation) => (
                    <TableRow key={reservation.id}>
                      <TableCell className="pl-8">{reservation.menteeName}</TableCell>

                      <TableCell className="pl-8">{reservation.content}</TableCell>

                      <TableCell>{formatDateTime(reservation.createdAt)}</TableCell>

                      <TableCell>{getStatusBadge(reservation.status)}</TableCell>

                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Select
                            value={
                              tempStatus[reservation.id] ??
                              reservation.status
                            }
                            onValueChange={(v) =>
                              handleStatusChange(
                                reservation.id,
                                v as Reservation["status"]
                              )
                            }
                            disabled={!!rowBusy[reservation.id]}
                          >
                            <SelectTrigger className="w-32">
                              <SelectValue />
                            </SelectTrigger>

                            <SelectContent>
                              <SelectItem value="PENDING">대기중</SelectItem>
                              <SelectItem value="APPROVED">승인됨</SelectItem>
                              <SelectItem value="REJECTED">거절됨</SelectItem>
                              <SelectItem value="COMPLETE">완료</SelectItem>
                            </SelectContent>
                          </Select>

                          <Button
                            size="sm"
                            variant="outline"
                            disabled={
                              !!rowBusy[reservation.id] ||
                              !tempStatus[reservation.id] ||
                              tempStatus[reservation.id] === reservation.status
                            }
                            onClick={() => handleStatusUpdate(reservation.id)}
                          >
                            수정
                          </Button>
                        </div>
                      </TableCell>

                      <TableCell className="text-right pr-8">
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
                              <AlertDialogTitle>예약 삭제</AlertDialogTitle>
                              <AlertDialogDescription>
                                정말로 이 예약을 삭제하시겠습니까?
                                <br />
                                <strong>{reservation.menteeName}</strong> 님의 예약이 삭제됩니다.
                              </AlertDialogDescription>
                            </AlertDialogHeader>

                            <AlertDialogFooter>
                              <AlertDialogCancel>취소</AlertDialogCancel>

                              <AlertDialogAction
                                className="bg-red-600 hover:bg-red-700"
                                onClick={() =>
                                  handleDeleteReservation(reservation.id)
                                }
                              >
                                삭제
                              </AlertDialogAction>
                            </AlertDialogFooter>
                          </AlertDialogContent>
                        </AlertDialog>
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
