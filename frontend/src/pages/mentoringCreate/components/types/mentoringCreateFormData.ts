export interface mentoringCreateFormData {
  price: number | null;
  category: string[];
  introduction: string;
  career: number | null;
  content: string;
  certificate: {
    type: string;
    title: string;
  }[];
}
