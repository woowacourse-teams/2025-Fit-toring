import { useEffect, useState } from "react";
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
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { Clock, Plus, Trash2 } from "lucide-react";
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

export function MentoringReservationSection({
  mentoringId,
}: MentoringReservationProps) {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const [tempStatus, setTempStatus] = useState<
    Record<number, Reservation["status"]>
  >({});
  const [rowBusy, setRowBusy] = useState<Record<number, boolean>>({});

  useEffect(() => {
    const load = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const data = await fetchReservations(mentoringId);
        setReservations(data);
      } catch (e) {
        console.error("예약 목록 조회 실패:", e);
        setError("예약 데이터를 불러오지 못했습니다.");
        setReservations([]);
      } finally {
        setIsLoading(false);
      }
    };

    if (mentoringId && !Number.isNaN(mentoringId)) {
      load();
    }
  }, [mentoringId]);

  const handleAddReservation = () => {
    // 추후 구현 포인트
    alert("예약 추가 기능을 구현해야 합니다.");
  };

  const handleStatusChange = (
    reservationId: number,
    newStatus: Reservation["status"],
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
          r.id === reservationId ? { ...r, status: newStatus } : r,
        ),
      );
      await fetchUpdateStatusReservation(reservationId, newStatus);

      setTempStatus((prev) => {
        const copy = { ...prev };
        delete copy[reservationId];
        return copy;
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
      setReservations((prev) =>
        prev.filter((r) => r.id !== reservationId),
      );
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
        return (
          <Badge
            variant="outline"
            className="bg-yellow-100 text-yellow-800"
          >
            대기중
          </Badge>
        );
      case "APPROVED":
        return (
          <Badge
            variant="outline"
            className="bg-green-100 text-green-800"
          >
            승인됨
          </Badge>
        );
      case "REJECTED":
        return (
          <Badge
            variant="outline"
            className="bg-red-100 text-red-800"
          >
            거절됨
          </Badge>
        );
      case "COMPLETE":
        return (
          <Badge
            variant="outline"
            className="bg-blue-100 text-blue-800"
          >
            완료
          </Badge>
        );
      default:
        return <Badge variant="outline">알 수 없음</Badge>;
    }
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
              {!isLoading && !error && (
                <>총 {reservations.length}개의 예약이 있습니다.</>
              )}
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
          <div className="text-center py-8">
            <p className="text-muted-foreground mb-4">
              예약된 멘토링이 없습니다.
            </p>
          </div>
        ) : (
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead className="pl-8">멘티 이름</TableHead>
                  <TableHead className="pl-8">신청문구</TableHead>
                  <TableHead>요청 시간</TableHead>
                  <TableHead>현재 상태</TableHead>
                  <TableHead>수정</TableHead>
                  <TableHead className="text-right pr-8">
                    삭제
                  </TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {reservations.map((reservation) => (
                  <TableRow key={reservation.id}>
                    <TableCell className="font-medium pl-8 py-6">
                      {reservation.menteeName}
                    </TableCell>
                    <TableCell className="font-medium pl-8 py-6">
                      {reservation.content}
                    </TableCell>
                    <TableCell className="py-3">
                      {formatDateTime(reservation.createdAt)}
                    </TableCell>
                    <TableCell className="py-3">
                      {getStatusBadge(reservation.status)}
                    </TableCell>
                    <TableCell className="py-3">
                      <div className="flex items-center gap-2">
                        <Select
                          value={
                            tempStatus[reservation.id] ??
                            reservation.status
                          }
                          onValueChange={(v) =>
                            handleStatusChange(
                              reservation.id,
                              v as Reservation["status"],
                            )
                          }
                          disabled={!!rowBusy[reservation.id]}
                        >
                          <SelectTrigger className="w-32">
                            <SelectValue />
                          </SelectTrigger>
                          <SelectContent>
                            <SelectItem value="PENDING">
                              대기중
                            </SelectItem>
                            <SelectItem value="APPROVED">
                              승인됨
                            </SelectItem>
                            <SelectItem value="REJECTED">
                              거절됨
                            </SelectItem>
                            <SelectItem value="COMPLETE">
                              완료
                            </SelectItem>
                          </SelectContent>
                        </Select>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() =>
                            handleStatusUpdate(reservation.id)
                          }
                          disabled={
                            !!rowBusy[reservation.id] ||
                            !tempStatus[reservation.id] ||
                            tempStatus[reservation.id] ===
                              reservation.status
                          }
                        >
                          수정
                        </Button>
                      </div>
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
                                예약 삭제 확인
                              </AlertDialogTitle>
                              <AlertDialogDescription>
                                정말로 이 예약을 삭제하시겠습니까?
                                <br />
                                <strong>
                                  {reservation.menteeName}
                                </strong>
                                님의 예약이 영구적으로 삭제됩니다.
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
                                  handleDeleteReservation(
                                    reservation.id,
                                  )
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
        )}
      </CardContent>
    </Card>
  );
}
