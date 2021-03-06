
import React from 'react';
import { ClipLoader } from 'react-spinners';
import './LoadingModal.css';

const LoadingModal = ({ show }) => {
    const showHideClassName = show ? "loading-modal loading-flex" : "loading-modal loading-none";
    return (
        <div className={showHideClassName}>
            <ClipLoader
                sizeUnit={"em"}
                size={4}
                color={'#123abc'}
                loading={show}
            />
        </div>
    );
};

export default LoadingModal;