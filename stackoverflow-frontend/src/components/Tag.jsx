import React from 'react'
import { Link } from 'react-router-dom'


export default function Tag({ name }){
return (
<Link to={`/?tag=${encodeURIComponent(name)}`} className="px-2 py-1 bg-gray-100 text-sm rounded">{name}</Link>
)
}