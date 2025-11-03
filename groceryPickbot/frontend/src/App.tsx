import { JSX } from "react";
import ReactButton from "./Button";
import { Routes, Route, Link } from "react-router-dom";
import Login from "./Login";
import Registration from "./Registration";
import WelcomePage from "./WelcomePage";
import ProductManagement from "./ProductManagement";
import OrderPage from "./OrderPage";

function Home(): JSX.Element {
  return (
      <div>
        <h1>Grocery Pickbot Store</h1>
        <div>
            <Link to="/registration"><ReactButton onPressFunc={()=> {}}>Register</ReactButton></Link>
            <Link to="/login"><ReactButton onPressFunc={()=> {}}>Login</ReactButton></Link>
        </div>
      </div>
  );
}

export default function App(): JSX.Element{
    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/registration" element={<Registration />} />
            <Route path="/welcome" element={<WelcomePage />} />
            <Route path="/productManagement" element={<ProductManagement />} />
            <Route path="/order" element={<OrderPage />} />
        </Routes>
    );
}