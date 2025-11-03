import React, {JSX, useState} from "react";
import {useNavigate} from "react-router-dom";
import ReactButton from "./Button";
import ErrorMessage from "./ErrorMessage";
import ReactInputField from "./InputField";
import RoleSelector from "./RoleSelector";
import {useUserMutation} from "./hooks/useUserMutation";

const initialState = {
    username: '',
    password: '',
    matchingPassword: '',
    role: 'USER',
    adminCode: ''
}; //JS object to hold form data

function Registration(): JSX.Element {
    const [formState, setFormState] = useState(initialState);
    const {execute: register, isLoading, errors} = useUserMutation('/api/auth/registration');
    const navigate = useNavigate();

    const handleFieldChange = (field: string, newValue: string) => {
        setFormState(prev => ({...prev, [field]: newValue}));
    };

    const handleRegistration = async (e: React.FormEvent) => {
        e.preventDefault();

        const successfulRegistration = await register(formState);
        if (successfulRegistration) {
            navigate("/login");
        }
    };
    return (
        <div className="form-container">
            <h1>Registration</h1>
            <form id="registrationForm" onSubmit={handleRegistration}>
                <ErrorMessage message={errors.general}/>

                <RoleSelector selectedRole={formState.role} onSelectRole={role => handleFieldChange('role', role)}/>

                {formState.role === 'ADMIN' && (
                    <>
                        <ErrorMessage message={errors.adminCode}/>
                        <ReactInputField
                            label="Admin Code"
                            type="password"
                            id="adminCode"
                            value={formState.adminCode}
                            onChange={e => handleFieldChange('adminCode', e.target.value)}
                        />
                    </>
                )}
                <ErrorMessage message={errors.username}/>
                <ReactInputField
                    label="Username"
                    type="text"
                    id="username"
                    value={formState.username}
                    onChange={e => handleFieldChange('username', e.target.value)}
                />

                <ErrorMessage message={errors.password}/>
                <ReactInputField
                    label="Password"
                    type="password"
                    id="password"
                    value={formState.password}
                    onChange={e => handleFieldChange('password', e.target.value)}
                />

                <ErrorMessage message={errors.matchingPassword}/>
                <ReactInputField
                    label="Confirm Password"
                    type="password"
                    id="matchingPassword"
                    value={formState.matchingPassword}
                    onChange={e => handleFieldChange('matchingPassword', e.target.value)}
                />


                <ReactButton type="submit" disabled={isLoading}>{isLoading ? 'Registering...' : 'Register'}</ReactButton>
            </form>
        </div>
    );
}

export default Registration;