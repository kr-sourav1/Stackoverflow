import React from "react";
import { Link } from "react-router-dom";

export default function Tag({ name }) {
  return (
    <Link
      to={`/?q=${encodeURIComponent(name)}`}
      className="
        inline-flex items-center
        rounded-full
        border border-border-subtle/70
        bg-surface-subtle/40
        px-3 py-1
        text-xs font-medium
        text-brand-300
        hover:bg-brand-500/10 hover:border-brand-400/80 hover:text-brand-200
        transition-colors duration-150
      "
    >
      {name}
    </Link>
  );
}
