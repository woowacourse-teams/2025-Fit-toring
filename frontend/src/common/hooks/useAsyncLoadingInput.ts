import { useCallback, useState } from 'react';

interface UseAsyncLoadingInputParams {
  callback: <T extends React.ChangeEvent<HTMLInputElement>>(
    e: T,
  ) => Promise<void>;
  onError: (error: unknown) => void;
}

const useAsyncLoadingInput = ({
  callback,
  onError,
}: UseAsyncLoadingInputParams) => {
  const [isLoading, setIsLoading] = useState(false);

  const asyncLoader = useCallback(
    <T>(callback: (e: T) => Promise<void>) =>
      async (e: T) => {
        setIsLoading(true);
        try {
          await callback(e);
        } catch (error) {
          onError(error);
        } finally {
          setIsLoading(false);
        }
      },
    [onError],
  );

  return { isLoading, handleCallback: asyncLoader(callback) };
};

export default useAsyncLoadingInput;
