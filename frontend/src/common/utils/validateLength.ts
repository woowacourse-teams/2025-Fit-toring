interface ValidateParams {
  min: number;
  max: number;
  value: string;
}

export const validateLength = ({ min, max, value }: ValidateParams) => {
  return value.length >= min && value.length <= max;
};
