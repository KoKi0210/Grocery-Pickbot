import React, { useState, JSX } from "react";
import ReactButton from "./Button";
import ReactInputField from "./InputField";
import { Product } from "./types";
import { useUpdateProduct } from "./hooks/useUpdateProduct";
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

export default function UpdateProductForm(): JSX.Element {

    const [updatedProduct, setUpdatedProduct] = useState(initialProduct);
    const [idToUpdate, setIdToUpdate] = useState('');
    const { updateProduct, isLoading, formMessage, isError } = useUpdateProduct();

    const handleFieldChange = (field: keyof typeof initialProduct, value: string | number) => {
        if (field === 'location') return;
        setUpdatedProduct(prev => ({ ...prev, [field]: value }));
    };

    const handleLocationChange = (coord: 'x' | 'y', value: string) => {
        setUpdatedProduct(prev => ({
            ...prev,
            location: {
                ...prev.location,
                [coord]: parseInt(value)
            }
        }));
    };

    const handleUpdate = async (e: React.FormEvent) => {
        e.preventDefault();

        const idToUpdateNum = parseInt(idToUpdate);
        if (isNaN(idToUpdateNum)) {
            return;
        }

        const success = await updateProduct(idToUpdateNum, updatedProduct);
        if (success) {
            setUpdatedProduct(initialProduct);
            setIdToUpdate('');
        }
    };

    return (
        <div className="form-container">
            <h2>Update product</h2>
            <form id="update-product-form" onSubmit={handleUpdate}>

                <ErrorMessage message={formMessage.notFound} />
                <ReactInputField label="id" type="text" id="id"
                                 value={idToUpdate} onChange={e => setIdToUpdate(e.target.value)} />

                <hr style={{margin: '20px 0'}} />

                <ErrorMessage message={formMessage.name}/>
                <ReactInputField label="Name" type="text" id="name"
                    value={updatedProduct.name} onChange={e => handleFieldChange('name', e.target.value)} />

                <ErrorMessage message={formMessage.quantity}/>
                <ReactInputField label="Quantity" type="number" id="quantity"
                    value={updatedProduct.quantity} onChange={e => handleFieldChange('quantity', e.target.value)} />

                <ErrorMessage message={formMessage.price}/>
                <ReactInputField label="Price" type="number" id="price"
                    value={updatedProduct.price} step={0.01} onChange={e => handleFieldChange('price', e.target.value)} />

                <ErrorMessage message={formMessage.location}/>
                <ErrorMessage message={formMessage.locationOccupied}/>
                <ReactInputField label="Location X" type="number" id="x"
                    value={updatedProduct.location.x} onChange={e => handleLocationChange('x', e.target.value)} />

                <ReactInputField label="Location Y" type="number" id="y"
                    value={updatedProduct.location.y} onChange={e => handleLocationChange('y', e.target.value)} />

                <ReactButton type="submit" disabled={isLoading} >
                    {isLoading ? 'Updating...' : 'Update Product'}
                </ReactButton>
            </form>
            {formMessage && (
                <div style={{ color: isError ? 'red' : 'green', marginTop: '10px' }}>
                    {formMessage.success || formMessage.general}
                </div>
            )}
        </div>
    );
}
