import {JSX, useState} from "react";
import ReactButton from "./Button";
import ReactInputField from "./InputField";
import { useDeleteProduct } from "./hooks/useDeleteProduct";

export default function DeleteProductForm(): JSX.Element {

    const [idToDelete, setIdToDelete] = useState('');

    const { deleteProduct, isLoading, isError, formMessage } = useDeleteProduct();

    const handleDelete = async (e: React.FormEvent) => {
        e.preventDefault();

        const success = await deleteProduct(idToDelete);
        if (success) {
            setIdToDelete('');
        }
    };

    return (
        <div className="form-container" style={{ marginTop: '20px' }}>
            <h2>Delete product</h2>
            <form id="delete-product-form" onSubmit={handleDelete}>

                <ReactInputField
                    label="Product ID to Delete"
                    type="text"
                    id="delete-id"
                    value={idToDelete}
                    onChange={e => setIdToDelete(e.target.value)}
                />

                <br />
                <ReactButton type="submit" disabled={isLoading}>
                    {isLoading ? 'Deleting...' : 'DELETE'}
                </ReactButton>
            </form>

            <div id="delete-result">
                {formMessage && (
                    <p style={{ color: isError ? 'red' : 'green' }}>
                        {formMessage}
                    </p>
                )}
            </div>
        </div>
    );
}