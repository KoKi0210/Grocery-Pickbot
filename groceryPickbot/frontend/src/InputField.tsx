import React, {FC} from "react";
import './style.css';

type ReactInputFieldProps = {
    label: string
    type: string;
    value: string | number;
    id: string;
    onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
    step?: number
}

const ReactInputField: FC<ReactInputFieldProps> = ({ label, type, id, value, onChange, step }) => {
 return(
     <div className="form-row">
         <label htmlFor={id}>{label}:</label>
            <input
                type={type}
                id={id}
                name={id}
                value={value}
                onChange={onChange}
                step={step}
            />
     </div>
     );
};

export default ReactInputField;