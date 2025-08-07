export interface mentoringCreateFormData {
  price: number;
  category: string[];
  introduction: string;
  career: number;
  content: string;
  certificate: {
    type: string;
    title: string;
  }[];
}
