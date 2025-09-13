import { useCallback, useState } from 'react';

interface UseAsyncLoadingInputParams {
  callback: <T extends React.ChangeEvent<HTMLInputElement>>(
    e: T,
  ) => Promise<void>;
  errorText: string;
}

const useAsyncLoadingInput = ({
  callback,
  errorText,
}: UseAsyncLoadingInputParams) => {
  const [isLoading, setIsLoading] = useState(false);

  const asyncLoader = useCallback(
    <T>(callback: (e: T) => Promise<void>) =>
      async (e: T) => {
        setIsLoading(true);
        try {
          await callback(e);
        } catch (error) {
          console.error(error);
          alert(errorText);
        } finally {
          setIsLoading(false);
        }
      },
    [errorText],
  );

  return { isLoading, handleCallback: asyncLoader(callback) };
};

export default useAsyncLoadingInput;
