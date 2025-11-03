import {JSX} from "react";
import {Link} from "react-router-dom";
import ReactButton from "./Button";

export default function WelcomePage(): JSX.Element {
    return (
        <div>
            <h1>Welcome to Grocery Pickbot Store</h1>
            <h2>Main menu</h2>
            <div>
                <Link to="/productManagement"><ReactButton onPressFunc={()=> {}}>🛠 Product management</ReactButton></Link>
                <Link to="/order"><ReactButton onPressFunc={()=> {}}>🛒 Make order</ReactButton></Link>
            </div>
        </div>
    );
}