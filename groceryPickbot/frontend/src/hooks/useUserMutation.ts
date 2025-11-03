import { useState } from 'react';

type ApiErrors = { [key: string]: string };
type ApiData = any;

export function useUserMutation(url: string) {

    const [isLoading, setIsLoading] = useState(false);
    const [errors, setErrors] = useState<ApiErrors>({});

    const execute = async (formData: ApiData): Promise<boolean> => {
        setIsLoading(true);
        setErrors({});

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            if (!response.ok) {
                const data = await response.json();
                setErrors(data);
                return false;
            }
            return true;
        } catch (err: any) {
            setErrors({ general: 'Network error. Please try again.' });
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    return { execute, isLoading, errors };
}