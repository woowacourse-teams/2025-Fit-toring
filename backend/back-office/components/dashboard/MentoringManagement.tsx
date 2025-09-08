import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../constants/routes";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "../ui/table";
import { Card } from "../ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../ui/dialog";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Textarea } from "../ui/textarea";
import { toast } from "sonner";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import { Badge } from "../ui/badge";
import { Button } from "../ui/button";
import { Checkbox } from "../ui/checkbox";
import { Plus, Upload, X, FileImage, User, Camera, Loader2 } from "lucide-react";
import { fetchMentorings, MentoringSummary, getUserList, getCategoryList, createMentoring, CreateMentoringRequest } from "../../services/mentoringApi";

// 멘토링 목록 타입
interface MentoringItem {
  id: string;
  mentorName: string;
  categories: string[];
  price: number;
}

// 자격증 정보 타입
interface CertificateInfo {
  type: "LICENSE" | "EDUCATION" | "AWARD" | "ETC";
  title: string;
  image: File | null;
}

// 카테고리 타입
interface Category {
  id: number;
  title: string;
}

// 사용자 타입
interface User {
  id: string;
  name: string;
  phoneNumber: string;
}

// 멘토링 등록 폼 데이터 타입
interface MentoringFormData {
  mentorId: string;
  profileImage: File | null;
  price: number;
  categoryIds: number[];
  introduction: string;
  career: number;
  content: string;
  certificateInfos: CertificateInfo[];
}

// 카테고리 더미 데이터
const mockCategories: Category[] = [
  { id: 1, title: "체형 교정" },
  { id: 2, title: "다이어트" },
  { id: 3, title: "벌크업" },
  { id: 4, title: "근력 강화" },
  { id: 5, title: "유연성·스트레칭" },
  { id: 6, title: "홈 트레이닝" },
  { id: 7, title: "재활 운동" },
  { id: 8, title: "식단 관리" },
];

// 사용자 더미 데이터
const mockUsers: User[] = [
  { id: "1", name: "김성현", phoneNumber: "010-1111-1111" },
  { id: "2", name: "박지훈", phoneNumber: "010-2222-1111" },
  { id: "3", name: "이수민", phoneNumber: "010-3333-1111" },
  { id: "4", name: "정대영", phoneNumber: "010-4444-1111" }
];

const categoryColors = [
  "bg-blue-100 text-blue-800",
  "bg-green-100 text-green-800",
  "bg-purple-100 text-purple-800",
  "bg-orange-100 text-orange-800",
  "bg-pink-100 text-pink-800",
  "bg-indigo-100 text-indigo-800",
];

export function MentoringManagement() {
  const navigate = useNavigate();

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [mentorings, setMentorings] = useState<MentoringItem[]>([]);
  const [userList, setUserList] = useState<User[]>([]);
  const [categoryList, setCategories] = useState<Category[]>(
    [],
  );
  const [isLoading, setIsLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<MentoringFormData>({
    mentorId: "",
    profileImage: null,
    price: 0,
    categoryIds: [],
    introduction: "",
    career: 0,
    content: "",
    certificateInfos: [],
  });
  const [error, setError] = useState<string | null>(null);

  const loadMentorings = async () => {
    try {
      setIsLoading(true);
      setError(null);
      const data: MentoringSummary[] = await fetchMentorings();

      // API 타입(MentoringSummary: id=number)을 UI 타입(MentoringItem: id=string)으로 변환
      setMentorings(
        data.map((d) => ({
          id: String(d.id),
          mentorName: d.mentorName,
          categories: d.categories ?? [],
          price: d.price,
        }))
      );
      console.log("✅ UI 상태 업데이트 완료:", { loadedCount: data.length });
    } catch (err) {
      setError("멘토링 데이터를 불러오는데 실패했습니다.");
      console.error("❌ 멘토링 로드 실패:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const loadUserList = async () => {
    try {
      const res = await getUserList();
      const users = Array.isArray(res) ? res : Array.isArray(res?.data) ? res.data : [];
      setUserList(users);
    } catch (error) {
      console.error("사용자 목록 로딩 실패:", error);
      toast.error("사용자 목록을 불러오는데 실패했습니다.");
    }
  };

  const loadCategoryList = async () => {
    try {
      const res = await getCategoryList();
      const categories = Array.isArray(res) ? res : Array.isArray(res?.data) ? res.data : [];
      setCategories(categories);
    } catch (error) {
      console.error("카테고리 목록 로딩 실패:", error);
      toast.error("카테고리 목록을 불러오는데 실패했습니다.");
    }
  };

  useEffect(() => {
    loadMentorings();
    loadUserList();
    loadCategoryList();
  }, []);

  const handleViewDetail = (mentoringId: string) => {
    navigate(ROUTES.getMentoringDetailPath(mentoringId));
  };

  const formatPrice = (price: number) =>
    new Intl.NumberFormat("ko-KR").format(price) + "원";

  const resetForm = () => {
    setFormData({
      mentorId: "",
      profileImage: null,
      price: 0,
      categoryIds: [],
      introduction: "",
      career: 0,
      content: "",
      certificateInfos: [],
    });
  };
  
  const handleCategoryToggle = (categoryId: number) => {
    setFormData((prev) => {
      if (prev.categoryIds.includes(categoryId)) {
        // 이미 선택된 카테고리를 해제
        return {
          ...prev,
          categoryIds: prev.categoryIds.filter(
            (id) => id !== categoryId,
          ),
        };
      } else {
        // 새로운 카테고리 선택
        if (prev.categoryIds.length >= 3) {
          alert(
            "카테고리는 최대 3개까지만 선택할 수 있습니다.",
          );
          return prev;
        }
        return {
          ...prev,
          categoryIds: [...prev.categoryIds, categoryId],
        };
      }
    });
  };
  
  const handleAddCertificate = () => {
    setFormData((prev) => ({
      ...prev,
      certificateInfos: [
        ...prev.certificateInfos,
        { type: "LICENSE", title: "", image: null },
      ],
    }));
  };
  
  const handleRemoveCertificate = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      certificateInfos: prev.certificateInfos.filter(
        (_, i) => i !== index,
      ),
    }));
  };

  const handleCertificateChange = (
    index: number,
    field: keyof CertificateInfo,
    value: any,
  ) => {
    setFormData((prev) => ({
      ...prev,
      certificateInfos: prev.certificateInfos.map((cert, i) =>
        i === index ? { ...cert, [field]: value } : cert,
      ),
    }));
  };

  const handleImageUpload = (
    index: number,
    file: File | null,
  ) => {
    handleCertificateChange(index, "image", file);
  };

  const handleProfileImageUpload = (file: File | null) => {
    setFormData((prev) => ({ ...prev, profileImage: file }));
  };

  const handleSubmit = async () => {
    // 필수 항목 검증
    if (!formData.mentorId) {
      toast.error("작성자를 선택해주세요.");
      return;
    }
    if (formData.price <= 0) {
      toast.error("가격을 입력해주세요.");
      return;
    }
    if (!formData.introduction.trim()) {
      toast.error("소개를 입력해주세요.");
      return;
    }
    if (!formData.content.trim()) {
      toast.error("상세 설명을 입력해주세요.");
      return;
    }

    // 자격증/학력/수상경력 정보가 있다면 모든 필드가 채워져야 함
    for (let i = 0; i < formData.certificateInfos.length; i++) {
      const cert = formData.certificateInfos[i];
      if (!cert.title.trim()) {
        toast.error(`${i + 1}번째 정보의 제목을 입력해주세요.`);
        return;
      }
      if (!cert.image) {
        toast.error(
          `${i + 1}번째 정보의 이미지를 업로드해주세요.`,
        );
        return;
      }
    }
    try {
      setIsSubmitting(true);

      // API 요청 데이터 준비
      const requestData: CreateMentoringRequest = {
        mentorId: formData.mentorId,
        profileImage: formData.profileImage || undefined,
        price: formData.price,
        categoryIds: formData.categoryIds,
        introduction: formData.introduction,
        career: formData.career,
        content: formData.content,
        certificateInfos: formData.certificateInfos.map(
          (cert) => ({
            type: cert.type,
            title: cert.title,
            image: cert.image || undefined,
          }),
        ),
      };

      // API 호출
      const response = await createMentoring(requestData);

      if (response.success) {
        toast.success("멘토링이 성공적으로 등록되었습니다.");

        // 성공 시 모달 닫기 및 폼 초기화
        setIsModalOpen(false);
        resetForm();

        // 목록 새로고침
        await loadMentorings();
      } else {
        toast.error("멘토링 등록에 실패했습니다.");
      }
    } catch (error: any) {
      console.error("멘토링 등록 실패:", error);
      toast.error("멘토링 등록 중 오류가 발생했습니다.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="space-y-3">
      {/* 헤더 */}
      <div className="flex justify-between items-center">
        <div>
          <h2>멘토링 관리</h2>
          <p className="text-muted-foreground">
            등록된 멘토링을 조회하고 관리할 수 있습니다.
          </p>
        </div>
        <Dialog
          open={isModalOpen}
          onOpenChange={setIsModalOpen}
        >
          <DialogTrigger asChild>
            <Button onClick={() => resetForm()}>
              <Plus className="h-4 w-4 mr-2" />새 멘토링 등록
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-4xl max-h-[85vh] flex flex-col p-6 mx-auto">
            <DialogHeader className="flex-shrink-0 mb-6">
              <DialogTitle>새 멘토링 등록</DialogTitle>
              <DialogDescription>
                새로운 멘토링을 등록합니다. 모든 필수 항목을
                입력해주세요.
              </DialogDescription>
            </DialogHeader>

            <div
              className="flex-1 overflow-y-auto px-6 modal-scroll-container"
              style={{
                maxHeight: "calc(85vh - 180px)",
                scrollBehavior: "smooth",
              }}
            >
              <div className="space-y-6 pb-4 w-full">
                {/* 작성자 선택 */}
                <div className="space-y-2">
                  <Label htmlFor="author">작성자 *</Label>
                  <Select
                    value={formData.mentorId}
                    onValueChange={(value) =>
                      setFormData((prev) => ({
                        ...prev,
                        mentorId: value,
                      }))
                    }
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="작성자를 선택해주세요" />
                    </SelectTrigger>
                    <SelectContent>
                      {(userList ?? []).map((user) => (
                        <SelectItem
                          key={user.id}
                          value={user.id}
                        >
                          {user.name} ({user.phoneNumber})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* 프로필 이미지 */}
                <div className="space-y-2">
                  <Label htmlFor="profileImage">
                    프로필 이미지
                  </Label>
                  <div
                    className="border-2 border-dashed border-gray-300 rounded-lg p-8 cursor-pointer hover:border-gray-400 hover:bg-gray-50 transition-colors"
                    onClick={() => {
                      const input = document.getElementById(
                        "profileImageInput",
                      ) as HTMLInputElement;
                      input?.click();
                    }}
                  >
                    {formData.profileImage ? (
                      <div className="flex items-center justify-center space-x-6">
                        <div className="relative">
                          <img
                            src={URL.createObjectURL(
                              formData.profileImage,
                            )}
                            alt="프로필 미리보기"
                            className="w-32 h-32 rounded-full object-cover border-4 border-gray-200 shadow-md"
                          />
                          <div className="absolute -bottom-2 -right-2 bg-green-500 text-white rounded-full p-2 shadow-lg">
                            <Camera className="h-5 w-5" />
                          </div>
                        </div>
                        <div className="text-center space-y-3">
                          <p className="text-base font-medium text-green-600">
                            {formData.profileImage.name}
                          </p>
                          <p className="text-sm text-gray-500">
                            (
                            {(
                              formData.profileImage.size /
                              1024 /
                              1024
                            ).toFixed(2)}{" "}
                            MB)
                          </p>
                          <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={(e) => {
                              e.stopPropagation();
                              handleProfileImageUpload(null);
                            }}
                          >
                            다른 이미지 선택
                          </Button>
                        </div>
                      </div>
                    ) : (
                      <div className="text-center space-y-6">
                        <div className="flex justify-center">
                          <div className="relative">
                            <div className="w-32 h-32 bg-gray-100 rounded-full flex items-center justify-center border-2 border-dashed border-gray-300 hover:border-gray-400 transition-colors">
                              <User className="h-16 w-16 text-gray-400" />
                            </div>
                            <div className="absolute -bottom-2 -right-2 bg-blue-500 text-white rounded-full p-2 shadow-lg">
                              <Upload className="h-5 w-5" />
                            </div>
                          </div>
                        </div>
                        <div>
                          <p className="text-base font-medium text-gray-700 mb-2">
                            프로필 이미지를 업로드해주세요
                            (선택사항)
                          </p>
                          <p className="text-sm text-gray-500 mb-2">
                            클릭하거나 파일을 드래그하여 업로드
                          </p>
                          <p className="text-xs text-gray-400">
                            JPG, PNG 파일만 업로드 가능 (최대
                            5MB)
                          </p>
                        </div>
                      </div>
                    )}
                    <input
                      id="profileImageInput"
                      type="file"
                      accept="image/jpeg,image/png,image/jpg"
                      onChange={(e) => {
                        const file =
                          e.target.files?.[0] || null;
                        if (
                          file &&
                          file.size > 5 * 1024 * 1024
                        ) {
                          alert(
                            "파일 크기는 5MB 이하로 업로드해주세요.",
                          );
                          return;
                        }
                        handleProfileImageUpload(file);
                      }}
                      className="hidden"
                    />
                  </div>
                </div>

                {/* 가격 */}
                <div className="space-y-2">
                  <Label htmlFor="price">가격 (원) *</Label>
                  <Input
                    id="price"
                    type="number"
                    min="0"
                    value={formData.price || ""}
                    onChange={(e) =>
                      setFormData((prev) => ({
                        ...prev,
                        price: Number(e.target.value),
                      }))
                    }
                    placeholder="가격을 입력해주세요"
                  />
                </div>

                {/* 카테고리 선택 */}
                <div className="space-y-2">
                  <Label>
                    카테고리 (최대 3개까지 선택 가능)
                  </Label>
                  <div className="grid grid-cols-2 md:grid-cols-3 gap-3 p-4 border rounded-lg">
                    {(categoryList ?? []).map((category) => (
                      <div
                        key={category.id}
                        className="flex items-center space-x-2"
                      >
                        <Checkbox
                          id={`category-${category.id}`}
                          checked={formData.categoryIds.includes(
                            category.id,
                          )}
                          onCheckedChange={() =>
                            handleCategoryToggle(category.id)
                          }
                        />
                        <Label
                          htmlFor={`category-${category.id}`}
                          className="text-sm font-normal cursor-pointer"
                        >
                          {category.title}
                        </Label>
                      </div>
                    ))}
                  </div>
                  {formData.categoryIds.length > 0 && (
                    <div className="space-y-2">
                      <p className="text-sm text-gray-600">
                        선택된 카테고리 (
                        {formData.categoryIds.length}/3)
                      </p>
                      <div className="flex flex-wrap gap-1">
                        {formData.categoryIds.map(
                          (categoryId) => {
                            const category = categoryList.find(
                              (c) => c.id === categoryId,
                            );
                            return category ? (
                              <Badge
                                key={categoryId}
                                variant="outline"
                              >
                                {category.title}
                              </Badge>
                            ) : null;
                          },
                        )}
                      </div>
                    </div>
                  )}
                </div>

                {/* 소개 */}
                <div className="space-y-2">
                  <Label htmlFor="introduction">소개 *</Label>
                  <Textarea
                    id="introduction"
                    value={formData.introduction}
                    onChange={(e) =>
                      setFormData((prev) => ({
                        ...prev,
                        introduction: e.target.value,
                      }))
                    }
                    placeholder="멘토링 소개를 간단히 입력해주세요"
                    rows={3}
                  />
                </div>

                {/* 경력 */}
                <div className="space-y-2">
                  <Label htmlFor="career">경력 (년)</Label>
                  <Input
                    id="career"
                    type="number"
                    min="0"
                    value={formData.career || ""}
                    onChange={(e) =>
                      setFormData((prev) => ({
                        ...prev,
                        career: Number(e.target.value),
                      }))
                    }
                    placeholder="경력을 년 단위로 입력해주세요"
                  />
                </div>

                {/* 상세 설명 */}
                <div className="space-y-2">
                  <Label htmlFor="content">상세 설명 *</Label>
                  <Textarea
                    id="content"
                    value={formData.content}
                    onChange={(e) =>
                      setFormData((prev) => ({
                        ...prev,
                        content: e.target.value,
                      }))
                    }
                    placeholder="멘토링에 대한 자세한 설명을 입력해주세요"
                    rows={5}
                  />
                </div>

                {/* 자격증/학력/수상경력 정보 */}
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <Label>
                      자격증/학력/수상경력 정보 (선택사항)
                    </Label>
                    <Button
                      type="button"
                      variant="outline"
                      size="sm"
                      onClick={handleAddCertificate}
                    >
                      <Plus className="h-4 w-4 mr-2" />
                      정보 추가
                    </Button>
                  </div>

                  {formData.certificateInfos.map(
                    (certificate, index) => (
                      <Card key={index} className="p-4">
                        <div className="space-y-4">
                          <div className="flex items-center justify-end">
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() =>
                                handleRemoveCertificate(index)
                              }
                              className="text-red-600 hover:text-red-700 hover:bg-red-50"
                            >
                              <X className="h-4 w-4" />
                            </Button>
                          </div>

                          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            {/* 자격증 타입 */}
                            <div className="space-y-2">
                              <Label>타입</Label>
                              <Select
                                value={certificate.type}
                                onValueChange={(
                                  value:
                                    | "LICENSE"
                                    | "EDUCATION"
                                    | "AWARD"
                                    | "ETC",
                                ) =>
                                  handleCertificateChange(
                                    index,
                                    "type",
                                    value,
                                  )
                                }
                              >
                                <SelectTrigger>
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                  <SelectItem value="LICENSE">
                                    자격증
                                  </SelectItem>
                                  <SelectItem value="EDUCATION">
                                    학력
                                  </SelectItem>
                                  <SelectItem value="AWARD">
                                    수상경력
                                  </SelectItem>
                                  <SelectItem value="ETC">
                                    기타
                                  </SelectItem>
                                </SelectContent>
                              </Select>
                            </div>

                            {/* 자격증 제목 */}
                            <div className="space-y-2">
                              <Label>제목</Label>
                              <Input
                                value={certificate.title}
                                onChange={(e) =>
                                  handleCertificateChange(
                                    index,
                                    "title",
                                    e.target.value,
                                  )
                                }
                                placeholder="자격증명, 학력, 수상명 등을 입력해주세요"
                              />
                            </div>
                          </div>

                          {/* 이미지 업로드 */}
                          <div className="space-y-2">
                            <Label>이미지</Label>
                            <div
                              className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center cursor-pointer hover:border-gray-400 hover:bg-gray-50 transition-colors"
                              onClick={() => {
                                const input =
                                  document.getElementById(
                                    `certificateImageInput-${index}`,
                                  ) as HTMLInputElement;
                                input?.click();
                              }}
                            >
                              {certificate.image ? (
                                <div className="space-y-4">
                                  <div className="flex justify-center">
                                    <div className="relative">
                                      <div className="w-24 h-24 bg-green-50 rounded-lg flex items-center justify-center border-2 border-green-200">
                                        <FileImage className="h-12 w-12 text-green-600" />
                                      </div>
                                      <div className="absolute -top-2 -right-2 bg-green-500 text-white rounded-full p-1">
                                        <Camera className="h-4 w-4" />
                                      </div>
                                    </div>
                                  </div>
                                  <div>
                                    <p className="text-base font-medium text-green-600">
                                      {certificate.image.name}
                                    </p>
                                    <p className="text-sm text-gray-500 mt-1">
                                      (
                                      {(
                                        certificate.image.size /
                                        1024 /
                                        1024
                                      ).toFixed(2)}{" "}
                                      MB)
                                    </p>
                                  </div>
                                  <Button
                                    type="button"
                                    variant="outline"
                                    size="sm"
                                    onClick={(e) => {
                                      e.stopPropagation();
                                      handleImageUpload(
                                        index,
                                        null,
                                      );
                                    }}
                                  >
                                    다른 이미지 선택
                                  </Button>
                                </div>
                              ) : (
                                <div className="space-y-4">
                                  <div className="flex justify-center">
                                    <div className="relative">
                                      <div className="w-24 h-24 bg-gray-100 rounded-lg flex items-center justify-center border-2 border-dashed border-gray-300 hover:border-gray-400 transition-colors">
                                        <FileImage className="h-12 w-12 text-gray-400" />
                                      </div>
                                      <div className="absolute -top-2 -right-2 bg-blue-500 text-white rounded-full p-1 shadow-lg">
                                        <Upload className="h-4 w-4" />
                                      </div>
                                    </div>
                                  </div>
                                  <div>
                                    <p className="text-base font-medium text-gray-700">
                                      증빙 이미지를
                                      업로드해주세요
                                    </p>
                                    <p className="text-sm text-gray-500 mt-1">
                                      클릭하거나 파일을
                                      드래그하여 업로드
                                    </p>
                                    <p className="text-xs text-gray-400 mt-1">
                                      모든 이미지 형식 지원
                                    </p>
                                  </div>
                                </div>
                              )}
                              <input
                                id={`certificateImageInput-${index}`}
                                type="file"
                                accept="image/*"
                                onChange={(e) => {
                                  const file =
                                    e.target.files?.[0] || null;
                                  handleImageUpload(
                                    index,
                                    file,
                                  );
                                }}
                                className="hidden"
                              />
                            </div>
                          </div>
                        </div>
                      </Card>
                    ),
                  )}

                  {formData.certificateInfos.length === 0 && (
                    <div className="text-center py-8 border-2 border-dashed border-gray-200 rounded-lg">
                      <FileImage className="h-12 w-12 mx-auto text-gray-400 mb-4" />
                      <p className="text-gray-500 mb-4">
                        자격증/학력/수상경력 정보가 없습니다
                      </p>
                    </div>
                  )}
                </div>
              </div>
            </div>

            <div className="flex-shrink-0 pt-6 mt-4 border-t">
              <DialogFooter className="gap-3">
                <Button
                  variant="outline"
                  onClick={() => {
                    setIsModalOpen(false);
                    resetForm();
                  }}
                  className="min-w-[80px]"
                >
                  취소
                </Button>
                <Button
                  onClick={handleSubmit}
                  disabled={isSubmitting}
                  className="min-w-[80px]"
                >
                  {isSubmitting ? (
                    <>
                      <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                      등록 중...
                    </>
                  ) : (
                    "등록"
                  )}
                </Button>
              </DialogFooter>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      {/* 멘토링 목록 테이블 */}
      <div className="space-y-3">
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="pl-8">멘토링 ID</TableHead>
                <TableHead>멘토명</TableHead>
                <TableHead>카테고리</TableHead>
                <TableHead>가격</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {mentorings.map((mentoring) => (
                <TableRow
                  key={mentoring.id}
                  className="cursor-pointer hover:bg-muted/50"
                  onClick={() => handleViewDetail(mentoring.id)}
                >
                  <TableCell className="font-medium pl-8 py-3">
                    {mentoring.id}
                  </TableCell>
                  <TableCell className="py-3">{mentoring.mentorName}</TableCell>
                  <TableCell className="py-3">
                    <div className="flex flex-wrap gap-1">
                      {mentoring.categories.slice(0, 3).map((category, index) => (
                        <Badge
                          key={`${category}-${index}`}
                          variant="outline"
                          className={categoryColors[index % categoryColors.length]}
                        >
                          {category}
                        </Badge>
                      ))}
                      {mentoring.categories.length > 3 && (
                        <Badge variant="outline" className="bg-gray-100 text-gray-600">
                          +{mentoring.categories.length - 3}
                        </Badge>
                      )}
                    </div>
                  </TableCell>
                  <TableCell className="py-3">{formatPrice(mentoring.price)}</TableCell>
                </TableRow>
              ))}
              {(!isLoading && mentorings.length === 0) && (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                    표시할 멘토링이 없습니다.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
      </div>
    </div>
  );
}
