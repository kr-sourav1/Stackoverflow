import React from 'react'


export default function AnswerCard({ a }){
return (
<div className="bg-white p-3 rounded shadow-sm">
<div className="flex gap-4">
<div className="w-16 text-center">
<div className="text-xl font-bold">{a.votes ?? 0}</div>
<div className="text-gray-500 text-sm">votes</div>
</div>
<div className="flex-1">
<div className="prose" dangerouslySetInnerHTML={{__html: a.content}} />
<div className="text-xs text-gray-500 mt-2">answered by {a.userName || 'anonymous'}</div>
</div>
</div>
</div>
)
}