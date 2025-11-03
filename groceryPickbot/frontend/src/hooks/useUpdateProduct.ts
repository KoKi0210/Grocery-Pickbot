import {useState} from "react";
import {Product} from "../types";

type ProductData = Omit<Product, 'id'>;

export function useUpdateProduct() {

    const [isLoading, setIsLoading] = useState(false);
    const [formMessage, setFormMessage] = useState<Record<string, string>>({});
    const [isError, setIsError] = useState(false);

    const updateProduct = async (id: number, product: ProductData): Promise<boolean> => {
        setIsLoading(true);
        setFormMessage({});
        setIsError(false);

        try {
            const response = await fetch(`/products/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(product)
            });

            if (!response.ok) {
                const errorMsg = await response.json();
                setFormMessage(errorMsg);
                setIsError(true)
                return false
            }

            setFormMessage({'success':'Successfully updated product!'});
            setIsError(false);
            return true;

        } catch (error: any) {
            setFormMessage(error.message);
            setIsError(true);
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    return {updateProduct, isLoading, formMessage, isError};
}