import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Card, CardContent, CardDescription, CardHeader, CardTitle,
} from "../ui/card";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "../ui/table";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import { Separator } from "../ui/separator";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "../ui/select";
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
  AlertDialogTrigger,
} from "../ui/alert-dialog";
import { ArrowLeft, Edit, Trash2, FileText, Tag, Clock, Plus, MessageSquare, Star } from "lucide-react";
import { ImageWithFallback } from "../figma/ImageWithFallback";
import { fetchMentoringDetail, MentoringDetail as MentoringDetailDTO, deleteMentoring } from "@/services/mentoringApi";
import { formatPrice, formatDateTime} from "@/utils/Formatter";
import {
  fetchReservations,
  Reservation,
  fetchUpdateStatusReservation,
  fetchDeleteReservation,
} from "@/services/reservationApi";

const categoryColors = [
  "bg-blue-100 text-blue-800",
  "bg-green-100 text-green-800",
  "bg-purple-100 text-purple-800",
  "bg-orange-100 text-orange-800",
  "bg-pink-100 text-pink-800",
  "bg-indigo-100 text-indigo-800",
];

// 리뷰 데이터 타입
interface ReviewData {
  id: number;
  menteeId: number;
  menteeName: string;
  rating: number;
  content: string;
  createdAt: string;
}

// 리뷰 목록 데이터 타입
interface ReviewListData {
  ratingAverage: string;
  ratingCount: number;
  reviewData: ReviewData[];
}

// 리뷰 더미 데이터 맵
const mockReviewData: Record<string, ReviewListData> = {
  1: {
    ratingAverage: "4.3",
    ratingCount: 3,
    reviewData: [
      {
        id: 1,
        menteeId: 1,
        menteeName: "이민수",
        rating: 4,
        content: "운동은 잘 알려주시는데 식단 봐주거나 그런건 없었어요. 하지만 체형 교정에는 도움이 많이 되었습니다.",
        createdAt: "2024-01-20T14:30:00Z",
      },
      {
        id: 2,
        menteeId: 2,
        menteeName: "김현주",
        rating: 5,
        content: "정말 만족스러운 멘토링이었습니다! 자세 교정도 꼼꼼히 봐주시고 운동 설명도 자세히 해주셔서 좋았어요.",
        createdAt: "2024-01-18T16:45:00Z",
      },
      {
        id: 3,
        menteeId: 3,
        menteeName: "박철수",
        rating: 4,
        content: "체형 교정 효과가 확실히 있었습니다. 다만 시간이 조금 짧게 느껴졌어요.",
        createdAt: "2024-01-15T11:20:00Z",
      },
    ],
  }
};
  
export function MentoringDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [mentoringData, setMentoringData] = useState<MentoringDetailDTO | null>(null);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [reviews, setReviews] = useState<ReviewListData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [tempStatus, setTempStatus] = useState<Record<number, Reservation["status"]>>({});
  const [isDeleting, setIsDeleting] = useState(false);
  const [rowBusy, setRowBusy] = useState<Record<number, boolean>>({});

  const numericId = id ? Number(id) : NaN;

  useEffect(() => {
    const load = async () => {
      if (!id) {
        setError("잘못된 접근입니다.");
        setIsLoading(false);
        return;
      }

      if (Number.isNaN(numericId)) {
        setError("유효하지 않은 멘토링 ID입니다.");
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError(null);

        // 멘토링 상세
        const data = await fetchMentoringDetail(numericId);
        setMentoringData(data);

        // 예약 목록 (숫자 ID로 호출)
        const reservationData = await fetchReservations(numericId);
        setReservations(reservationData);
        setReviews(mockReviewData[String(numericId)] ?? null);
      } catch (e) {
        setError("멘토링 데이터를 불러오지 못했습니다.");
        setMentoringData(null);
      } finally {
        setIsLoading(false);
      }
    };

    load();
  }, [id]);

  const handleDeleteMentoring = async () => {
    if (Number.isNaN(numericId)) return;
    try {
      setIsDeleting(true);
      await deleteMentoring(numericId);
      navigate(`/web-admin#mentoring`);
    } catch (e) {
      console.error("멘토링 삭제 실패:", e);
    } finally {
      setIsDeleting(false);
    }
  };

  // 예약 상태 수정
  const handleStatusUpdate = async (reservationId: number) => {
    const newStatus = tempStatus[reservationId];
    if (!newStatus || Number.isNaN(numericId)) return;
  
    const prevReservations = reservations;
  
    try {
      setRowBusy((p) => ({ ...p, [reservationId]: true }));
      setReservations((prev) =>
        prev.map((r) => (r.id === reservationId ? { ...r, status: newStatus } : r)),
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

  // 예약 삭제
  const handleDeleteReservation = async (reservationId: number) => {
    if (Number.isNaN(numericId)) return;
  
    const prevReservations = reservations;
    try {
      setRowBusy((p) => ({ ...p, [reservationId]: true }));
      setReservations((prev) => prev.filter((r) => r.id !== reservationId));
  
      await fetchDeleteReservation(reservationId);
    } catch (e) {
      console.error("예약 삭제 실패:", e);
      setReservations(prevReservations);
    } finally {
      setRowBusy((p) => ({ ...p, [reservationId]: false }));
    }
  };

  const handleBackToList = () => navigate(`/web-admin#mentoring`);
  const handleAddReservation = () => alert("예약 추가 기능을 구현해야 합니다.");

  const handleStatusChange = (reservationId: number, newStatus: Reservation["status"]) => {
    setTempStatus((prev) => ({ ...prev, [reservationId]: newStatus }));
  };

  const handleDeleteReview = (reviewId: number) => {
    if (reviews) {
      const updatedReviewData = reviews.reviewData.filter(
        (review) => review.id !== reviewId,
      );
      const newRatingCount = updatedReviewData.length;
      
      // 평균 평점 재계산
      let newRatingAverage = "0";
      if (newRatingCount > 0) {
        const totalRating = updatedReviewData.reduce(
          (sum, review) => sum + review.rating,
          0,
        );
        newRatingAverage = (totalRating / newRatingCount).toFixed(1);
      }

      setReviews({
        ratingAverage: newRatingAverage,
        ratingCount: newRatingCount,
        reviewData: updatedReviewData,
      });
    }
  };

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, index) => (
      <Star
        key={index}
        className={`h-4 w-4 ${
          index < rating
            ? "text-yellow-400 fill-yellow-400"
            : "text-gray-300"
        }`}
      />
    ));
  };

  const getStatusBadge = (status: Reservation["status"]) => {
    switch (status) {
      case "PENDING":
        return <Badge variant="outline" className="bg-yellow-100 text-yellow-800">대기중</Badge>;
      case "APPROVED":
        return <Badge variant="outline" className="bg-green-100 text-green-800">승인됨</Badge>;
      case "REJECTED":
        return <Badge variant="outline" className="bg-red-100 text-red-800">거절됨</Badge>;
      case "COMPLETE":
        return <Badge variant="outline" className="bg-blue-100 text-blue-800">완료</Badge>;
      default:
        return <Badge variant="outline">알 수 없음</Badge>;
    }
  };

  if (isLoading) {
    return <div className="flex justify-center items-center h-64">로딩 중...</div>;
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <h3>{error}</h3>
        <Button onClick={handleBackToList} className="mt-4" variant="outline">
          <ArrowLeft className="h-4 w-4 mr-2" />
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  if (!mentoringData) {
    return (
      <div className="text-center py-12">
        <h3>멘토링을 찾을 수 없습니다</h3>
        <Button onClick={handleBackToList} className="mt-4" variant="outline">
          <ArrowLeft className="h-4 w-4 mr-2" />
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 액션 버튼 */}
      <div className="flex justify-end gap-2">
        <Button variant="outline">
          <Edit className="h-4 w-4 mr-2" />
          수정
        </Button>
        <AlertDialog>
          <AlertDialogTrigger asChild>
            <Button variant="destructive" disabled={isDeleting}>
              <Trash2 className="h-4 w-4 mr-2" />
              {isDeleting ? "삭제 중..." : "삭제"}
            </Button>
          </AlertDialogTrigger>

          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>멘토링 삭제 확인</AlertDialogTitle>
              <AlertDialogDescription>
                정말로 이 멘토링을 삭제하시겠습니까?<br />
                <strong>이 작업은 되돌릴 수 없습니다.</strong>
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel disabled={isDeleting}>취소</AlertDialogCancel>
              <AlertDialogAction
                className="bg-red-600 hover:bg-red-700"
                onClick={handleDeleteMentoring}
                disabled={isDeleting}
              >
                삭제
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>

      {/* 멘토링 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" />
            멘토링 정보
          </CardTitle>
          <CardDescription>멘토링 ID: {mentoringData.id}</CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          {/* 멘토 기본 정보 */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-4">
              {mentoringData.profileImageUrl && (
                <div className="flex justify-center md:justify-start">
                  <ImageWithFallback
                    src={mentoringData.profileImageUrl}
                    alt={`${mentoringData.mentorName} 프로필`}
                    className="w-20 h-20 rounded-full object-cover border"
                  />
                </div>
              )}

              <div className="text-center md:text-left space-y-2">
                <h3>{mentoringData.mentorName}</h3>
                <p className="text-muted-foreground">경력 {mentoringData.career}년</p>
                <p className="text-muted-foreground">가격 {formatPrice(mentoringData.price)} / 세션</p>
              </div>
            </div>
          </div>

          <Separator />

          {/* 한줄소개(= introduction) */}
          <div>
            <label className="font-medium">한줄소개</label>
            <p className="mt-1 text-muted-foreground">{mentoringData.introduction}</p>
          </div>

          {/* 카테고리 */}
          <div>
            <label className="font-medium flex items-center gap-2">
              <Tag className="h-4 w-4" />
              카테고리
            </label>
            <div className="flex flex-wrap gap-2 mt-2">
              {mentoringData.categories.map((category, index) => (
                <Badge
                  key={`${category}-${index}`}
                  variant="outline"
                  className={categoryColors[index % categoryColors.length]}
                >
                  {category}
                </Badge>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 상세 내용 */}
      <Card>
        <CardHeader>
          <CardTitle>상세 내용</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="prose prose-sm max-w-none">
            <pre className="whitespace-pre-wrap font-sans">{mentoringData.content}</pre>
          </div>
        </CardContent>
      </Card>

      {/* 예약 목록 */}
      <Card>
        <CardHeader>
          <div className="flex justify-between items-start">
            <div>
              <CardTitle className="flex items-center gap-2">
                <Clock className="h-5 w-5" />
                예약 목록
              </CardTitle>
              <CardDescription>총 {reservations.length}개의 예약이 있습니다.</CardDescription>
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
              <p className="text-muted-foreground mb-4">예약된 멘토링이 없습니다.</p>
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
                    <TableHead className="text-right pr-8">삭제</TableHead>
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
                          value={tempStatus[reservation.id] || reservation.status}
                          onValueChange={(v) => handleStatusChange(reservation.id, v as Reservation["status"])}
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
                            variant="outline"
                            size="sm"
                            onClick={() => handleStatusUpdate(reservation.id)}
                            disabled={
                              !!rowBusy[reservation.id] ||
                              !tempStatus[reservation.id] ||
                              tempStatus[reservation.id] === reservation.status
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
                              <Button variant="ghost" size="sm" className="text-red-600 hover:text-red-700 hover:bg-red-50">
                                <Trash2 className="h-4 w-4 mr-2" />
                                삭제
                              </Button>
                            </AlertDialogTrigger>
                            <AlertDialogContent>
                              <AlertDialogHeader>
                                <AlertDialogTitle>예약 삭제 확인</AlertDialogTitle>
                                <AlertDialogDescription>
                                  정말로 이 예약을 삭제하시겠습니까?
                                  <br />
                                  <strong>{reservation.menteeName}</strong>님의 예약이 영구적으로 삭제됩니다.
                                  <br />이 작업은 되돌릴 수 없습니다.
                                </AlertDialogDescription>
                              </AlertDialogHeader>
                              <AlertDialogFooter>
                                <AlertDialogCancel>취소</AlertDialogCancel>
                                <AlertDialogAction
                                  onClick={() => handleDeleteReservation(reservation.id)}
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

      {/* 리뷰 목록 */}
      <Card>
        <CardHeader>
          <div className="flex justify-between items-start">
            <div>
              <CardTitle className="flex items-center gap-2">
                <MessageSquare className="h-5 w-5" />
                리뷰 목록
              </CardTitle>
              <CardDescription>
                {reviews ? (
                  <div className="flex items-center gap-2 mt-2">
                    <div className="flex items-center gap-1">
                      {renderStars(Math.round(parseFloat(reviews.ratingAverage)))}
                      <span className="ml-2 font-medium">
                        {reviews.ratingAverage}
                      </span>
                    </div>
                    <span className="text-muted-foreground">
                      (총 {reviews.ratingCount}건)
                    </span>
                  </div>
                ) : (
                  "아직 리뷰가 없습니다."
                )}
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
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead className="pl-8">
                      리뷰 ID
                    </TableHead>
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
                  {reviews.reviewData.map((review) => (
                    <TableRow key={review.id}>
                      <TableCell className="font-medium pl-8 py-6">
                        {review.id}
                      </TableCell>
                      <TableCell className="py-3">
                        {review.menteeName}({review.menteeId})
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
                                    {review.menteeName}({review.menteeName})
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
          )}
        </CardContent>
      </Card>                   
    </div>
  );
}
