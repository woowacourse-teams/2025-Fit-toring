export interface MyProfileType {
  name: string;
  phoneNumber: string;
  loginId: string;
  imageUrl: string | null;
}

export interface MyProfileResponse {
  data: MyProfileType;
}
