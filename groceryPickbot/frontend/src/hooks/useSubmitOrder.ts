import { useState } from "react";
import {OrderResponse} from "../types";

type OrderItem = { productId: number; quantity: number };
type OrderData = OrderItem[];

export function useSubmitOrder() {
    const [isLoading, setIsLoading] = useState(false);
    const [orderResult, setOrderResult] = useState<OrderResponse | null>(null);
    const [networkError, setNetworkError] = useState<string | null>(null);

    const submitOrder = async (orderItems: OrderData): Promise<boolean> => {
        setIsLoading(true);
        setOrderResult(null);
        setNetworkError(null);

        if (orderItems.length === 0){
            setNetworkError('Order must contain at least one item.');
            setIsLoading(false);
            return false;
        }

        try {
            const response = await fetch('/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ items: orderItems })
            });

            const data = await response.json();

            if (!response.ok){
                if (data.status === "FAIL") {
                    setOrderResult(data);
                } else {
                    setNetworkError(data.default || 'An unexpected error occurred');
                }
                return false;
            }

            setOrderResult(data as OrderResponse);
            return response.ok;

        } catch (err: any) {
            setNetworkError('Network error. Please try again.');
            return false;
        } finally {
            setIsLoading(false);
        }
    };
    return { submitOrder, isLoading, orderResult, networkError };
}

