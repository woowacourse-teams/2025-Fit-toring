import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "../ui/card";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import { Separator } from "../ui/separator";
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
import { ArrowLeft, Edit, FileText, Tag, Trash2 } from "lucide-react";
import { ImageWithFallback } from "../figma/ImageWithFallback";
import {
  fetchMentoringDetail,
  MentoringDetail as MentoringDetailDTO,
  deleteMentoring,
} from "@/services/mentoringApi";
import { formatPrice } from "@/utils/Formatter";
import { MentoringReservationSection } from "./MentoringReservation";
import { MentoringReviewSection } from "./MentoringReview";

const categoryColors = [
  "bg-blue-100 text-blue-800",
  "bg-green-100 text-green-800",
  "bg-purple-100 text-purple-800",
  "bg-orange-100 text-orange-800",
  "bg-pink-100 text-pink-800",
  "bg-indigo-100 text-indigo-800",
];

export function MentoringDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [mentoringData, setMentoringData] =
    useState<MentoringDetailDTO | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [isDeleting, setIsDeleting] = useState<boolean>(false);

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

        const data = await fetchMentoringDetail(numericId);
        setMentoringData(data);
      } catch (e) {
        console.error("멘토링 상세 조회 실패:", e);
        setError("멘토링 데이터를 불러오지 못했습니다.");
        setMentoringData(null);
      } finally {
        setIsLoading(false);
      }
    };

    load();
  }, [id, numericId]);

  const handleBackToList = () => navigate(`/web-admin#mentoring`);

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

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        로딩 중...
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <h3>{error}</h3>
        <Button
          onClick={handleBackToList}
          className="mt-4"
          variant="outline"
        >
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
        <Button
          onClick={handleBackToList}
          className="mt-4"
          variant="outline"
        >
          <ArrowLeft className="h-4 w-4 mr-2" />
          목록으로 돌아가기
        </Button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 상단 액션 버튼 */}
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
                정말로 이 멘토링을 삭제하시겠습니까?
                <br />
                <strong>이 작업은 되돌릴 수 없습니다.</strong>
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel disabled={isDeleting}>
                취소
              </AlertDialogCancel>
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

      {/* 멘토링 기본 정보 */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="h-5 w-5" />
            멘토링 정보
          </CardTitle>
          <CardDescription>
            멘토링 ID: {mentoringData.id}
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
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
                <p className="text-muted-foreground">
                  경력 {mentoringData.career}년
                </p>
                <p className="text-muted-foreground">
                  가격 {formatPrice(mentoringData.price)} / 세션
                </p>
              </div>
            </div>
          </div>

          <Separator />

          {/* 한줄소개 */}
          <div>
            <label className="font-medium">한줄소개</label>
            <p className="mt-1 text-muted-foreground">
              {mentoringData.introduction}
            </p>
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
                  className={
                    categoryColors[
                      index % categoryColors.length
                    ]
                  }
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
            <pre className="whitespace-pre-wrap font-sans">
              {mentoringData.content}
            </pre>
          </div>
        </CardContent>
      </Card>

      {/* 예약 섹션 */}
      {!Number.isNaN(numericId) && (
        <MentoringReservationSection mentoringId={numericId} />
      )}

      {/* 리뷰 섹션 */}
      {!Number.isNaN(numericId) && (
        <MentoringReviewSection mentoringId={numericId} />
      )}
    </div>
  );
}
