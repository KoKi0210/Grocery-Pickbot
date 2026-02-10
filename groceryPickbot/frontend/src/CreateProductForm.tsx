import React, { useState, JSX } from "react";
import ReactInputField from "./InputField";
import ReactButton from "./Button";
import { Product } from "./types";
import { useCreateProduct } from "./hooks/useCreateProduct";
import ErrorMessage from "./ErrorMessage";

const initialProduct: Omit<Product, 'id'> = {
    name: ' ',
    quantity: 0,
    price: 0,
    location: {
        x: 0,
        y: 0
    }
};

export default function CreateProductForm(): JSX.Element {
    const [product, setProduct] = useState<Omit<Product, 'id'>>(initialProduct);
    const { createProduct, isLoading, formMessage, isError } = useCreateProduct();

    const handleFieldChange = (field: keyof Omit<Product, 'id'>, value: string | number) => {
        if (field === 'location') return;
        setProduct(prev => ({ ...prev, [field]: value }));
    };

    const handleLocationChange = (coord: 'x' | 'y', value: string) => {
        setProduct(prev => ({
            ...prev,
            location: {
                ...prev.location,
                [coord]: parseInt(value)
            }
        }));
    };

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();

        const success = await createProduct(product);
        if (success) {
            setProduct(initialProduct);
        }
    };

    return (
        <div className="form-container" style={{ marginTop: '20px' }}>
            <h2>Create product</h2>
            <form id="create-product-form" onSubmit={handleCreate}>

                <ErrorMessage message={formMessage.general}/>

                <ErrorMessage message={formMessage.name}/>
                <ReactInputField label="Name" type="text" id="name"
                    value={product.name} onChange={e => handleFieldChange('name', e.target.value)} />

                <ErrorMessage message={formMessage.quantity}/>
                <ReactInputField label="Quantity" type="number" id="quantity"
                    value={product.quantity} onChange={e => handleFieldChange('quantity', e.target.value)} />

                <ErrorMessage message={formMessage.price}/>
                <ReactInputField label="Price" type="number" id="price"
                    value={product.price} step={0.01} onChange={e => handleFieldChange('price', e.target.value)} />

                <ErrorMessage message={formMessage.location}/>
                <ErrorMessage message={formMessage.locationOccupied}/>
                <ReactInputField label="Location X" type="number" id="x"
                    value={product.location.x} onChange={e => handleLocationChange('x', e.target.value)} />

                <ReactInputField label="Location Y" type="number" id="y"
                    value={product.location.y} onChange={e => handleLocationChange('y', e.target.value)} />
                <br />

                <ErrorMessage message={formMessage.authentication}/>
                <ReactButton type="submit" disabled={isLoading}>
                    {isLoading ? 'Creating...' : 'CREATE'}
                </ReactButton>
            </form>

            <div id="create-result">
                {formMessage && (
                    <p style={{ color: isError ? 'red' : 'green' }}>
                        {formMessage.success || formMessage.general}
                    </p>
                )}
            </div>
        </div>
    );
}