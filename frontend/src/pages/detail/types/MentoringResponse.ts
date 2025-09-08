import type { CertificateResponse } from './CertificatesResponse';

export interface MentoringResponse {
  id: number;
  mentorName: string;
  ratingAverage: string;
  ratingCount: number;
  categories: string[];
  price: number;
  career: number;
  chatUrl: string;
  profileImageUrl: string | null;
  introduction: string;
  content: string;
  certificates: CertificateResponse[];
}
