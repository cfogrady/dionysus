
import React from 'react';

import './SimpleModal.css';

const SimpleModal = ({ show, children }) => {
    const showHideClassName = show ? "modal display-block" : "modal display-none";
    return (
      <div className={showHideClassName}>
        <section className="modal-main">
          {children}
        </section>
      </div>
    );
  };

export default SimpleModal;