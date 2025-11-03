import React, {JSX, useState} from "react";
import {useProductFetch} from "./hooks/useProductFetch";
import {Product} from "./types";
import ReactButton from "./Button";
import {useSubmitOrder} from "./hooks/useSubmitOrder";
import BotTracking from "./BotTracking";
import "./style.css";
import ErrorMessage from "./ErrorMessage";


export default function OrderPage(): JSX.Element {

    const {products, isLoading: isLoadingProducts, error: productsError} = useProductFetch<Product[]>();

    const {submitOrder, isLoading: isSubmitting, orderResult, networkError} = useSubmitOrder();

    const [quantities, setQuantities] = useState<Record<string, number>>({});

    const handleQuantityChange = (productId: number, value: string) => {
        const quantity = parseInt(value) || 0;

        setQuantities(prevQuantities => ({
            ...prevQuantities,
            [productId]: quantity
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const orderItems = Object.entries(quantities)
            .filter(([id, qty]) => qty > 0)
            .map(([id, qty]) => ({
                productId: parseInt(id),
                quantity: qty
            }));

        const success = await submitOrder(orderItems);

        if (success) {
            setQuantities({});
        }
    };

    if (isLoadingProducts) return <p>Loading products...</p>;
    if (productsError) return <ErrorMessage message={productsError}/>;
    if (!products) return <p>No products found.</p>;

    return (
        <div className="form-container">
            <h2>Create order</h2>
            <form onSubmit={handleSubmit}>
                <table border={1} cellPadding="5">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Price</th>
                        <th>Available</th>
                        <th>Quantity to Order</th>
                    </tr>
                    </thead>
                    <tbody>
                    {products.map(p => (
                        <tr key={p.id}>
                            <td>{p.name}</td>
                            <td>{p.price}</td>
                            <td>{p.quantity}</td>
                            <td>
                                <input
                                    type="number"
                                    min="0"
                                    value={quantities[p.id] || 0}
                                    onChange={e => handleQuantityChange(p.id, e.target.value)}
                                />
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>

                <br/>
                <ReactButton type="submit" disabled={isSubmitting}>
                    {isSubmitting ? 'Placing order...' : 'Finish order'}
                </ReactButton>
            </form>

            <div className="order-result-container">
                {networkError && <ErrorMessage message={networkError}/>}

                {orderResult && (
                    <div>
                        {orderResult.status === "SUCCESS" && (
                            <BotTracking result={orderResult}/>
                        )}

                        {orderResult.status === "FAIL" && (
                            <>
                                <ErrorMessage message={'❌' + orderResult.message}/>
                                {orderResult.missingItems && orderResult.missingItems.length > 0 && (
                                    <div>
                                        <strong>Missing products:</strong>
                                        <ul>
                                            {orderResult.missingItems.map((item, index) => (
                                                <li key={index}>
                                                    {item.productName} — requested: {item.requested},
                                                    available: {item.available}
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                )}
                            </>)}
                    </div>)}
            </div>
        </div>
    );
}
