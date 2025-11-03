import {useState, useEffect} from "react";

export function useProductFetch<T>() {
    const [products, setProducts] = useState<T | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true)
            setError(null)
            try {
                const response = await fetch('/products');

                if (!response.ok) {
                    const errorMsg = await response.text();
                    setProducts([] as T);
                    setError(errorMsg || 'Failed to fetch products');
                    return;
                }

                const jsonData = await response.json();
                setProducts(jsonData);

            } catch (err: any) {
                setError(err.message || 'Network error. Please try again.');
            } finally {
                setIsLoading(false);
            }
        }
        fetchData();
    }, []);
    return {
        products, isLoading, error
    };
}