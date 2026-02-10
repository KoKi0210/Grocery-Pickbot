import { useState } from "react";

export function useDeleteProduct()  {

    const [formMessage, setFormMessage] = useState<Record<string, string>>({});
    const [isError, setIsError] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const deleteProduct = async (id: string): Promise<boolean> => {
        setIsLoading(true);
        setFormMessage({});
        setIsError(false);

        try {
            const response = await fetch(`/products/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                setFormMessage({'success':`Successfully deleted product with ID: ${id}`});
                setIsError(false);
                return true;
            } else {
                const errorMsg = await response.json();
                setFormMessage(errorMsg)
                setIsError(true)
                return false;
            }
        } catch (error: any) {
            setFormMessage(error.message);
            setIsError(true);
            return false;
        } finally {
            setIsLoading(false);
        }
    };
    return{deleteProduct , isLoading, formMessage, isError};
}