import React, {JSX} from "react";
import {OrderSuccessResponse} from "./types";
import ReactButton from "./Button";
import {useRouteFetch} from "./hooks/useRouteFetch";
import "./style.css";

export default function BotTracking({result}: { result: OrderSuccessResponse }): JSX.Element {
    const {fetchRoutes, isLoading, routes, error} = useRouteFetch();

    const handleCollectOrder = (isParallel: boolean) => {
        fetchRoutes(result.orderId, isParallel);
    };
    return (
        <div id="track-section">
            <p className="success-message">✅ {result.message}</p>
            <p>
                Order ID:
                <input type="text" value={result.orderId} className="order-id-input" readOnly/>
            </p>
            <ReactButton onPressFunc={() => handleCollectOrder(false)} disabled={isLoading}>{isLoading ? 'Loading...' : 'Single Bot Collect'}</ReactButton>
            <ReactButton onPressFunc={() => handleCollectOrder(true)} disabled={isLoading}>{isLoading ? 'Loading...' : 'Parallel Bots Collect'}</ReactButton>

            <div id="bot-path">
                {isLoading && <p>Loading routes...</p>}
                {error && <p className="error-message">{error}</p>}

                {routes && (
                    <div>
                        <strong>Routes:</strong><br/>
                        {routes.map((route, idx) => (
                            <div key={idx} className="route-item">
                                <b>Route for {route.routeName || `Route ${idx + 1}`}:</b>

                                {route.visitedLocations && route.visitedLocations.length > 0 ? (
                                    ` ${route.visitedLocations.map(loc => `(${loc[0]}, ${loc[1]})`).join(' ➡️ ')}`
                                ) : (
                                    ' ❌ No locations found.'
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
}

