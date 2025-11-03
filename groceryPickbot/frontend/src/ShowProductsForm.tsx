import React, { JSX } from 'react';
import ProductTable from "./ProductTable";
import { Product } from "./types";
import { useProductFetch } from "./hooks/useProductFetch";
import ErrorMessage from "./ErrorMessage";


export default function AllProductsList(): JSX.Element {

    const { products: products, isLoading, error } = useProductFetch<Product[]>();

    if (isLoading) {
        return <p>Loading products...</p>;
    }

    if (error) {
        return <ErrorMessage message={error} />;
    }

    return <ProductTable products={products || []} />
}