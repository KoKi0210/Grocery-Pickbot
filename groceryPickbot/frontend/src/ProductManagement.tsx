import React, {JSX, useState} from "react";
import {Link} from "react-router-dom";
import ReactButton from "./Button";
import CreateProductForm from "./CreateProductForm";
import AllProductsList from "./ShowProductsForm";
import DeleteProductForm from "./DeleteProductForm";
import UpdateProductForm from "./UpdateProductForm";

export default function ProductManagement(): JSX.Element {
    const [activeView, setActiveView] = useState<string | null>(null);

    return (
        <div>
            <h1>Product Management</h1>
            <div>
                <ReactButton onPressFunc={() => setActiveView("create")}>Create product</ReactButton>
                <ReactButton onPressFunc={() => setActiveView("update")}>Update product</ReactButton>
                <ReactButton onPressFunc={() => setActiveView("delete")}>Delete product</ReactButton>
                <ReactButton onPressFunc={() => setActiveView("list")}>Show all products</ReactButton>
                <Link to="/welcome"><ReactButton onPressFunc={()=> {}}>Main menu</ReactButton></Link>
            </div>

            <hr />
            <div>
                {activeView === 'create' && <CreateProductForm />}
                {activeView === "update" && <UpdateProductForm />}
                {activeView === "delete" && <DeleteProductForm />}
                {activeView === "list" && <AllProductsList />}
            </div>
        </div>
    );
}

