import React, { FunctionComponent } from 'react'
import './style.css'

type ReactButtonProps = {
    onPressFunc?: () => void;
    children: React.ReactNode;
    type?: "submit" | "button" | "reset";
    disabled?: boolean;
}

const ReactButton: FunctionComponent<ReactButtonProps> = ({ onPressFunc, children, type = "button", disabled = false }) => {
    return <button className="button-style" type = {type} onClick={onPressFunc} disabled={disabled}>{ children }</button>
};

export default ReactButton;