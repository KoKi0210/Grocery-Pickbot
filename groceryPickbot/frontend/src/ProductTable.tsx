import React, { JSX } from 'react';
import { Product } from "./types";



type ProductTableProps = {
    products: Product[];
}

export default function ProductTable({ products }: ProductTableProps): JSX.Element {

    return (
        <div className="form-container">
            <h2>Products</h2>

            {products.length === 0 ? (
                <p>Products not found.</p>
            ) : (
                <table border={1} cellPadding="5">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Quantity</th>
                        <th>Price</th>
                        <th>Location (X, Y)</th>
                    </tr>
                    </thead>
                    <tbody>
                    {products.map(p => (
                        <tr key={p.id}>
                            <td>{p.id}</td>
                            <td>{p.name}</td>
                            <td>{p.quantity}</td>
                            <td>{p.price}</td>
                            <td>({p.location.x}, {p.location.y})</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}