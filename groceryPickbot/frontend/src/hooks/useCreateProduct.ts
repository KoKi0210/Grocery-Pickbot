import { useState } from 'react';
import { Product } from '../types';

type ProductData = Omit<Product, 'id'>;

export function useCreateProduct() {

    const [isLoading, setIsLoading] = useState(false);
    const [formMessage, setFormMessage] = useState<Record<string, string>>({});
    const [isError, setIsError] = useState(false);

    const createProduct = async (product: ProductData): Promise<boolean> => {
        setIsLoading(true);
        setFormMessage({});
        setIsError(false);

        try {
            const response = await fetch('/products', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(product)
            });

            if (!response.ok) {
                const errorMsg = await response.json();
                setFormMessage(errorMsg);
                setIsError(true);
                return false;
            }

            setFormMessage({'success':'Successfully created product!'});
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

    return { createProduct, isLoading, formMessage, isError };
}