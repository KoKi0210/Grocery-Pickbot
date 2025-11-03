import React, {JSX, useState} from "react";
import { useNavigate } from "react-router-dom";
import ReactButton from "./Button";
import ErrorMessage from "./ErrorMessage";
import ReactInputField from "./InputField";
import "./style.css";
import { useUserMutation } from "./hooks/useUserMutation";

const initialState = {
    username: '',
    password: ''
}; //JS object to hold form data

function Login(): JSX.Element {
    const [formState, setFormState] = useState(initialState);
    const {execute: login, isLoading, errors } = useUserMutation('/api/auth/login');
    const navigate = useNavigate();

    const handleFieldChange = (field: string, newValue: string) => {
        setFormState(prev => ({ ...prev, [field]: newValue }));
    };

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();

        const successfulLogin = await login(formState);
        if (successfulLogin) {
            navigate("/welcome");
        }
    };

    return (
        <div className="form-container">
            <h1>Login</h1>
            <form id="loginForm" onSubmit={handleLogin} >
                <ErrorMessage message={errors.error} />

                <ReactInputField
                    label="Username"
                    type="text"
                    id="username"
                    value={formState.username}
                    onChange={(e) => handleFieldChange('username' ,e.target.value)}
                />

                <ReactInputField
                    label="Password"
                    type="password"
                    id="password"
                    value={formState.password}
                    onChange={(e) => handleFieldChange('password' ,e.target.value)}
                />

                <ReactButton type="submit" disabled={isLoading}>{isLoading ? 'Logging...' : 'Login'}</ReactButton>
            </form>
        </div>
    );
}

export default Login;