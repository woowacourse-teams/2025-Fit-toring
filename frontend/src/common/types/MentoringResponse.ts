export interface MentoringResponse {
  id: number;
  mentorName: string;
  ratingAverage: string;
  ratingCount: number;
  categories: string[];
  price: number;
  career: number;
  profileImageUrl: string | null;
  introduction: string;
  content: string;
  certificates: CertificateResponse[];
}

export interface CertificateResponse {
  certificateId: string;
  title: string;
  type: string;
  imageUrl: string;
}
