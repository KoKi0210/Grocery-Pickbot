import {useState} from "react";
import {BotRoute} from "../types";

export function useRouteFetch<T>() {
    const [isLoading, setIsLoading] = useState(false);
    const [routes, setRoutes] = useState<BotRoute[] | null>(null);
    const [error, setError] = useState<string | null>(null);

    const fetchRoutes = async (orderId: number, collectInParallel: boolean) => {
        setIsLoading(true);
        setError(null);
        setRoutes(null)

        try {
            const response = await fetch(`/routes?orderId=${orderId}&collectInParallel=${collectInParallel}`);

            if (!response.ok) {
                setError(await response.text() || 'Failed to fetch routes');
                return;
            }

            const data: BotRoute = await response.json();

            if (Array.isArray(data) && data.length > 0) {
                setRoutes(data);
            } else {
                setError('No routes found for the given order ID.');
            }
        } catch (err: any) {
            setError(err.message || 'Error loading the routes.');
        } finally {
            setIsLoading(false);
        }
    };

    return{fetchRoutes, isLoading, routes, error};
}